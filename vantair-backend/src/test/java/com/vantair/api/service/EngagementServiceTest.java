package com.vantair.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vantair.api.dto.Dtos.ContactRequest;
import com.vantair.api.dto.Dtos.PartnerRequest;
import com.vantair.api.model.ContactMessage;
import com.vantair.api.model.NewsletterSubscriber;
import com.vantair.api.model.PartnerEnquiry;
import com.vantair.api.repository.ContactMessageRepository;
import com.vantair.api.repository.NewsletterSubscriberRepository;
import com.vantair.api.repository.PartnerEnquiryRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EngagementServiceTest {

    @Mock NewsletterSubscriberRepository newsletter;
    @Mock ContactMessageRepository contacts;
    @Mock PartnerEnquiryRepository partners;
    @InjectMocks EngagementService service;

    @Test
    void subscribe_new_saves() {
        when(newsletter.existsById("a@b.com")).thenReturn(false);
        service.subscribe("a@b.com");

        ArgumentCaptor<NewsletterSubscriber> captor = ArgumentCaptor.forClass(NewsletterSubscriber.class);
        verify(newsletter).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("a@b.com");
    }

    @Test
    void subscribe_existing_isNoOp() {
        when(newsletter.existsById("a@b.com")).thenReturn(true);
        service.subscribe("a@b.com");
        verify(newsletter, never()).save(any());
    }

    @Test
    void listSubscribers_delegates() {
        when(newsletter.findAll()).thenReturn(List.of(new NewsletterSubscriber()));
        assertThat(service.listSubscribers()).hasSize(1);
    }

    @Test
    void submitContact_mapsAllFields() {
        when(contacts.save(any(ContactMessage.class))).thenAnswer(i -> i.getArgument(0));
        ContactMessage saved = service.submitContact(
                new ContactRequest("Rahul", "r@b.com", "+91", "General", "Hi"));

        assertThat(saved.getName()).isEqualTo("Rahul");
        assertThat(saved.getEmail()).isEqualTo("r@b.com");
        assertThat(saved.getPhone()).isEqualTo("+91");
        assertThat(saved.getType()).isEqualTo("General");
        assertThat(saved.getMessage()).isEqualTo("Hi");
    }

    @Test
    void listContacts_delegates() {
        when(contacts.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(new ContactMessage()));
        assertThat(service.listContacts()).hasSize(1);
    }

    @Test
    void submitPartner_mapsAllFields() {
        when(partners.save(any(PartnerEnquiry.class))).thenAnswer(i -> i.getArgument(0));
        PartnerEnquiry saved = service.submitPartner(
                new PartnerRequest("Anjali", "Mehta Ltd", "a@m.com", "+91", "Reseller", "500/mo", "msg"));

        assertThat(saved.getName()).isEqualTo("Anjali");
        assertThat(saved.getCompany()).isEqualTo("Mehta Ltd");
        assertThat(saved.getVolume()).isEqualTo("500/mo");
        assertThat(saved.getType()).isEqualTo("Reseller");
    }

    @Test
    void listPartners_delegates() {
        when(partners.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(new PartnerEnquiry()));
        assertThat(service.listPartners()).hasSize(1);
    }
}
