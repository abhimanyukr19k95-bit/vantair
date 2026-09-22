package com.vantair.api.service;

import com.vantair.api.dto.Dtos.ContactRequest;
import com.vantair.api.dto.Dtos.PartnerRequest;
import com.vantair.api.model.ContactMessage;
import com.vantair.api.model.NewsletterSubscriber;
import com.vantair.api.model.PartnerEnquiry;
import com.vantair.api.repository.ContactMessageRepository;
import com.vantair.api.repository.NewsletterSubscriberRepository;
import com.vantair.api.repository.PartnerEnquiryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Newsletter sign-ups, contact messages and partner enquiries. */
@Service
public class EngagementService {

    private final NewsletterSubscriberRepository newsletter;
    private final ContactMessageRepository contacts;
    private final PartnerEnquiryRepository partners;

    public EngagementService(NewsletterSubscriberRepository newsletter,
                             ContactMessageRepository contacts,
                             PartnerEnquiryRepository partners) {
        this.newsletter = newsletter;
        this.contacts = contacts;
        this.partners = partners;
    }

    @Transactional
    public void subscribe(String email) {
        if (!newsletter.existsById(email)) {
            NewsletterSubscriber sub = new NewsletterSubscriber();
            sub.setEmail(email);
            newsletter.save(sub);
        }
    }

    @Transactional(readOnly = true)
    public List<NewsletterSubscriber> listSubscribers() {
        return newsletter.findAll();
    }

    @Transactional
    public ContactMessage submitContact(ContactRequest req) {
        ContactMessage msg = new ContactMessage();
        msg.setName(req.name());
        msg.setEmail(req.email());
        msg.setPhone(req.phone());
        msg.setType(req.type());
        msg.setMessage(req.message());
        return contacts.save(msg);
    }

    @Transactional(readOnly = true)
    public List<ContactMessage> listContacts() {
        return contacts.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public PartnerEnquiry submitPartner(PartnerRequest req) {
        PartnerEnquiry enquiry = new PartnerEnquiry();
        enquiry.setName(req.name());
        enquiry.setCompany(req.company());
        enquiry.setEmail(req.email());
        enquiry.setPhone(req.phone());
        enquiry.setType(req.type());
        enquiry.setVolume(req.volume());
        enquiry.setMessage(req.message());
        return partners.save(enquiry);
    }

    @Transactional(readOnly = true)
    public List<PartnerEnquiry> listPartners() {
        return partners.findAllByOrderByCreatedAtDesc();
    }
}
