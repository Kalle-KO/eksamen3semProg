package com.example.Eksamensprojekt3sem.FireEvent;

import com.example.Eksamensprojekt3sem.Enum.Status;
import com.example.Eksamensprojekt3sem.Siren.SirenModel;
import com.example.Eksamensprojekt3sem.Siren.SirenRepository;
import com.example.Eksamensprojekt3sem.Siren.SirenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FireEventServiceTest {

    @Mock
    SirenRepository sirenRepo;

    @Mock
    SirenService sirenService;

    @Mock
    FireEventRepository fireRepo;

    @InjectMocks
    FireEventService service;

    @Test
    void registerEventShouldActivateNearbySirens() {
        // Arrange: to sirener — én nær og én langt væk
        SirenModel near = new SirenModel();
        near.setSirenId(1);
        near.setLatitude(10);
        near.setLongitude(10);
        near.setStatus(Status.NEUTRAL);
        near.setDisabled(false);
        near.setFireEvents(new HashSet<>());

        SirenModel far = new SirenModel();
        far.setSirenId(2);
        far.setLatitude(20);
        far.setLongitude(20);
        far.setStatus(Status.NEUTRAL);
        far.setDisabled(false);
        far.setFireEvents(new HashSet<>());

        when(sirenRepo.findAll()).thenReturn(List.of(near, far));
        when(sirenService.calculateDistance(10, 10, 10, 10)).thenReturn(0.0);
        when(sirenService.calculateDistance(10, 10, 20, 20)).thenReturn(100.0);

        // Når vi gemmer et FireEvent, skal det returnere samme instans med ID=1
        when(fireRepo.save(any(FireEventModel.class))).thenAnswer(invocation -> {
            FireEventModel e = invocation.getArgument(0);
            e.setFireEventId(1); // Vi gemmer Fire Event med ID 1, er dybest set det hele der sker i dette stykke.
            return e;
        });

        // Act
        FireEventModel evt = service.registerEvent(10, 10);

        // Assert: near-sirenen skal nu være FARLIG
        assertEquals(Status.EMERGENCY, near.getStatus());

        // Assert: det registrerede event indeholder kun near-sirenen
        assertTrue(evt.getSirens().contains(near));
        assertFalse(evt.getSirens().contains(far));

        // Verify at vi gemmer de opdaterede sirener
        verify(sirenRepo).saveAll(evt.getSirens());
    }

    @Test
    void closeEventShouldResetStatuses() {
        // Arrange: en sirene som er FARLIG på et åbent event
        SirenModel s1 = new SirenModel();
        s1.setSirenId(1);
        s1.setStatus(Status.EMERGENCY);

        FireEventModel evt = new FireEventModel();
        evt.setFireEventId(1);
        evt.setSirens(new HashSet<>(Set.of(s1)));
        evt.setClosed(false);

        when(fireRepo.findById(1)).thenReturn(Optional.of(evt));
        when(fireRepo.save(any(FireEventModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        FireEventModel closed = service.closeEvent(1);

        // Assert: event er lukket
        assertTrue(closed.isClosed());

        // Assert: sirenen er sat tilbage til FRED
        assertEquals(Status.NEUTRAL, s1.getStatus());

        // Verify at vi gemmer de opdaterede sirener
        verify(sirenRepo).saveAll(anySet());
    }
}
