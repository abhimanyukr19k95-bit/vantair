// ============================================
// VANTAIR BACKEND API SERVER
// Node.js/Express with MongoDB
// Production-Ready Backend Architecture
// ============================================

// ===== server.js =====
const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const mongoose = require('mongoose');
const nodemailer = require('nodemailer');
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');

dotenv.config();

const app = express();

// ===== MIDDLEWARE =====
app.use(helmet()); // Security headers
app.use(cors({
  origin: process.env.FRONTEND_URL || 'http://localhost:3000',
  credentials: true
}));
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ limit: '10mb', extended: true }));

// Rate limiting
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100 // limit each IP to 100 requests per windowMs
});
app.use('/api/', limiter);

// ===== DATABASE CONNECTION =====
mongoose.connect(process.env.MONGODB_URI || 'mongodb://localhost:27017/vantair', {
  useNewUrlParser: true,
  useUnifiedTopology: true,
}).then(() => console.log('MongoDB connected'))
  .catch(err => console.log('MongoDB connection error:', err));

// ===== DATABASE SCHEMAS =====

// Contact Form Schema
const ContactSchema = new mongoose.Schema({
  name: { type: String, required: true },
  email: { type: String, required: true },
  company: { type: String },
  message: { type: String, required: true },
  type: { type: String, enum: ['general', 'partnership', 'support'], default: 'general' },
  createdAt: { type: Date, default: Date.now },
  status: { type: String, enum: ['new', 'read', 'responded'], default: 'new' }
});

// Partnership Application Schema
const PartnershipSchema = new mongoose.Schema({
  companyName: { type: String, required: true },
  contactName: { type: String, required: true },
  email: { type: String, required: true },
  phone: { type: String, required: true },
  website: { type: String },
  industry: { type: String },
  partnershipType: { 
    type: String, 
    enum: ['distribution', 'affiliate', 'sub-partner', 'multi-service'],
    required: true 
  },
  serviceInterests: [{ type: String }],
  annualRevenue: { type: String },
  geographicCoverage: { type: String },
  additionalInfo: { type: String },
  createdAt: { type: Date, default: Date.now },
  status: { type: String, enum: ['pending', 'under-review', 'approved', 'rejected'], default: 'pending' }
});

// Newsletter Schema
const NewsletterSchema = new mongoose.Schema({
  email: { type: String, required: true, unique: true },
  subscribedAt: { type: Date, default: Date.now },
  status: { type: String, enum: ['active', 'unsubscribed'], default: 'active' }
});

// Service Data Schema
const ServiceSchema = new mongoose.Schema({
  name: { type: String, required: true },
  subtitle: { type: String },
  description: { type: String },
  details: { type: String },
  features: [{ type: String }],
  icon: { type: String },
  createdAt: { type: Date, default: Date.now },
  updatedAt: { type: Date, default: Date.now }
});

// Partner Data Schema
const PartnerSchema = new mongoose.Schema({
  name: { type: String, required: true },
  category: { type: String },
  description: { type: String },
  website: { type: String },
  logoUrl: { type: String },
  offerings: [{ type: String }],
  createdAt: { type: Date, default: Date.now },
  updatedAt: { type: Date, default: Date.now }
});

// Models
const Contact = mongoose.model('Contact', ContactSchema);
const Partnership = mongoose.model('Partnership', PartnershipSchema);
const Newsletter = mongoose.model('Newsletter', NewsletterSchema);
const Service = mongoose.model('Service', ServiceSchema);
const PartnerData = mongoose.model('Partner', PartnerSchema);

// ===== EMAIL CONFIGURATION =====
const transporter = nodemailer.createTransport({
  service: process.env.EMAIL_SERVICE || 'gmail',
  auth: {
    user: process.env.EMAIL_USER,
    pass: process.env.EMAIL_PASSWORD
  }
});

// ===== API ROUTES =====

// Health Check
app.get('/api/health', (req, res) => {
  res.json({ status: 'Server is running', timestamp: new Date() });
});

// ===== CONTACT ENDPOINTS =====

// Submit contact form
app.post('/api/contact', async (req, res) => {
  try {
    const { name, email, company, message, type } = req.body;

    // Validation
    if (!name || !email || !message) {
      return res.status(400).json({ error: 'Name, email, and message are required' });
    }

    // Save to database
    const contact = new Contact({ name, email, company, message, type });
    await contact.save();

    // Send confirmation email to user
    await transporter.sendMail({
      from: process.env.EMAIL_USER,
      to: email,
      subject: 'We received your message - VANTAIR',
      html: `
        <h2>Thank you, ${name}!</h2>
        <p>We've received your message and will get back to you soon.</p>
        <p><strong>Message:</strong> ${message}</p>
        <p>Best regards,<br/>VANTAIR Team</p>
      `
    });

    // Send notification email to admin
    await transporter.sendMail({
      from: process.env.EMAIL_USER,
      to: process.env.ADMIN_EMAIL,
      subject: `New Contact Form Submission - ${type}`,
      html: `
        <h3>New Contact Form Submission</h3>
        <p><strong>Name:</strong> ${name}</p>
        <p><strong>Email:</strong> ${email}</p>
        <p><strong>Company:</strong> ${company || 'N/A'}</p>
        <p><strong>Type:</strong> ${type}</p>
        <p><strong>Message:</strong> ${message}</p>
      `
    });

    res.status(201).json({ 
      message: 'Contact form submitted successfully',
      contactId: contact._id 
    });
  } catch (error) {
    console.error('Error submitting contact form:', error);
    res.status(500).json({ error: 'Failed to submit contact form' });
  }
});

// Get all contacts (admin only)
app.get('/api/contact/admin/all', async (req, res) => {
  try {
    // In production, add authentication middleware here
    const contacts = await Contact.find().sort({ createdAt: -1 });
    res.json(contacts);
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch contacts' });
  }
});

// ===== PARTNERSHIP ENDPOINTS =====

// Submit partnership application
app.post('/api/partnership/apply', async (req, res) => {
  try {
    const {
      companyName,
      contactName,
      email,
      phone,
      website,
      industry,
      partnershipType,
      serviceInterests,
      annualRevenue,
      geographicCoverage,
      additionalInfo
    } = req.body;

    // Validation
    if (!companyName || !contactName || !email || !partnershipType) {
      return res.status(400).json({ 
        error: 'Company name, contact name, email, and partnership type are required' 
      });
    }

    // Save to database
    const partnership = new Partnership({
      companyName,
      contactName,
      email,
      phone,
      website,
      industry,
      partnershipType,
      serviceInterests,
      annualRevenue,
      geographicCoverage,
      additionalInfo
    });
    await partnership.save();

    // Send confirmation email
    await transporter.sendMail({
      from: process.env.EMAIL_USER,
      to: email,
      subject: 'Partnership Application Received - VANTAIR',
      html: `
        <h2>Thank you for your interest, ${contactName}!</h2>
        <p>We've received your partnership application and our team will review it shortly.</p>
        <p><strong>Application ID:</strong> ${partnership._id}</p>
        <p><strong>Partnership Type:</strong> ${partnershipType}</p>
        <p>We'll be in touch within 48 hours.</p>
        <p>Best regards,<br/>VANTAIR Partnership Team</p>
      `
    });

    // Send admin notification
    await transporter.sendMail({
      from: process.env.EMAIL_USER,
      to: process.env.ADMIN_EMAIL,
      subject: `New Partnership Application - ${companyName}`,
      html: `
        <h3>New Partnership Application</h3>
        <p><strong>Company:</strong> ${companyName}</p>
        <p><strong>Contact:</strong> ${contactName}</p>
        <p><strong>Email:</strong> ${email}</p>
        <p><strong>Phone:</strong> ${phone}</p>
        <p><strong>Partnership Type:</strong> ${partnershipType}</p>
        <p><strong>Services Interested:</strong> ${serviceInterests.join(', ')}</p>
        <p><a href="${process.env.ADMIN_URL}/partnerships/${partnership._id}">View Application</a></p>
      `
    });

    res.status(201).json({ 
      message: 'Partnership application submitted successfully',
      applicationId: partnership._id,
      status: 'pending'
    });
  } catch (error) {
    console.error('Error submitting partnership application:', error);
    res.status(500).json({ error: 'Failed to submit partnership application' });
  }
});

// Get partnership applications (admin only)
app.get('/api/partnership/admin/all', async (req, res) => {
  try {
    const partnerships = await Partnership.find().sort({ createdAt: -1 });
    res.json(partnerships);
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch partnerships' });
  }
});

// Get partnership by ID
app.get('/api/partnership/:id', async (req, res) => {
  try {
    const partnership = await Partnership.findById(req.params.id);
    if (!partnership) {
      return res.status(404).json({ error: 'Partnership not found' });
    }
    res.json(partnership);
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch partnership' });
  }
});

// Update partnership status (admin only)
app.patch('/api/partnership/:id/status', async (req, res) => {
  try {
    const { status } = req.body;
    const partnership = await Partnership.findByIdAndUpdate(
      req.params.id,
      { status },
      { new: true }
    );
    res.json(partnership);
  } catch (error) {
    res.status(500).json({ error: 'Failed to update partnership' });
  }
});

// ===== NEWSLETTER ENDPOINTS =====

// Subscribe to newsletter
app.post('/api/newsletter/subscribe', async (req, res) => {
  try {
    const { email } = req.body;

    if (!email) {
      return res.status(400).json({ error: 'Email is required' });
    }

    const existingSubscriber = await Newsletter.findOne({ email });
    if (existingSubscriber) {
      return res.status(400).json({ error: 'Already subscribed' });
    }

    const subscriber = new Newsletter({ email });
    await subscriber.save();

    // Send welcome email
    await transporter.sendMail({
      from: process.env.EMAIL_USER,
      to: email,
      subject: 'Welcome to VANTAIR Newsletter',
      html: `
        <h2>Welcome to VANTAIR!</h2>
        <p>Thank you for subscribing to our newsletter.</p>
        <p>You'll receive updates on partnerships, services, and industry insights.</p>
        <p>Best regards,<br/>VANTAIR Team</p>
      `
    });

    res.status(201).json({ message: 'Successfully subscribed to newsletter' });
  } catch (error) {
    console.error('Error subscribing to newsletter:', error);
    res.status(500).json({ error: 'Failed to subscribe' });
  }
});

// Unsubscribe from newsletter
app.post('/api/newsletter/unsubscribe', async (req, res) => {
  try {
    const { email } = req.body;
    await Newsletter.findOneAndUpdate({ email }, { status: 'unsubscribed' });
    res.json({ message: 'Successfully unsubscribed' });
  } catch (error) {
    res.status(500).json({ error: 'Failed to unsubscribe' });
  }
});

// ===== SERVICES ENDPOINTS =====

// Get all services
app.get('/api/services', async (req, res) => {
  try {
    const services = await Service.find();
    res.json(services);
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch services' });
  }
});

// Get service by ID
app.get('/api/services/:id', async (req, res) => {
  try {
    const service = await Service.findById(req.params.id);
    if (!service) {
      return res.status(404).json({ error: 'Service not found' });
    }
    res.json(service);
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch service' });
  }
});

// ===== PARTNERS ENDPOINTS =====

// Get all partners
app.get('/api/partners', async (req, res) => {
  try {
    const partners = await PartnerData.find();
    res.json(partners);
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch partners' });
  }
});

// Get partner by ID
app.get('/api/partners/:id', async (req, res) => {
  try {
    const partner = await PartnerData.findById(req.params.id);
    if (!partner) {
      return res.status(404).json({ error: 'Partner not found' });
    }
    res.json(partner);
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch partner' });
  }
});

// ===== ANALYTICS ENDPOINTS =====

// Get dashboard stats (admin only)
app.get('/api/analytics/stats', async (req, res) => {
  try {
    const totalContacts = await Contact.countDocuments();
    const totalApplications = await Partnership.countDocuments();
    const pendingApplications = await Partnership.countDocuments({ status: 'pending' });
    const activeSubscribers = await Newsletter.countDocuments({ status: 'active' });

    const recentContacts = await Contact.find().sort({ createdAt: -1 }).limit(5);
    const recentApplications = await Partnership.find().sort({ createdAt: -1 }).limit(5);

    res.json({
      totalContacts,
      totalApplications,
      pendingApplications,
      activeSubscribers,
      recentContacts,
      recentApplications
    });
  } catch (error) {
    res.status(500).json({ error: 'Failed to fetch analytics' });
  }
});

// ===== ERROR HANDLING =====

app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ error: 'Something went wrong!' });
});

app.use((req, res) => {
  res.status(404).json({ error: 'Route not found' });
});

// ===== START SERVER =====

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
  console.log(`Environment: ${process.env.NODE_ENV || 'development'}`);
});

module.exports = app;

// ============================================
// END OF BACKEND SERVER
// ============================================