import React, { useState, useEffect } from 'react';
import { Menu, X, ChevronRight, ArrowRight, CheckCircle2, MapPin, Phone, Mail, Linkedin, Twitter, Facebook } from 'lucide-react';

// ============================================
// PREMIUM B2B WEBSITE FOR VANTAIR
// Production-Grade Frontend with Animations
// ============================================

// Smooth scroll animation component
const useScrollAnimation = () => {
  const [isVisible, setIsVisible] = useState(false);
  const ref = React.useRef(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsVisible(true);
          observer.unobserve(entry.target);
        }
      },
      { threshold: 0.1 }
    );

    if (ref.current) observer.observe(ref.current);
    return () => observer.disconnect();
  }, []);

  return [ref, isVisible];
};

// Navigation Component
const Navigation = ({ mobileMenuOpen, setMobileMenuOpen }) => {
  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-white/80 backdrop-blur-md border-b border-gray-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <div className="flex items-center space-x-2">
            <div className="w-10 h-10 bg-gradient-to-br from-blue-600 to-blue-800 rounded-lg flex items-center justify-center text-white font-bold text-lg">V</div>
            <span className="text-xl font-bold text-gray-900">VANTAIR</span>
          </div>

          {/* Desktop Menu */}
          <div className="hidden md:flex items-center space-x-8">
            {['Services', 'Partners', 'About', 'Contact'].map((item) => (
              <a
                key={item}
                href={`#${item.toLowerCase()}`}
                className="text-gray-700 hover:text-blue-600 transition-colors font-medium"
              >
                {item}
              </a>
            ))}
            <button className="bg-gradient-to-r from-blue-600 to-blue-700 text-white px-6 py-2 rounded-lg hover:shadow-lg transition-shadow font-medium">
              Partner With Us
            </button>
          </div>

          {/* Mobile Menu Button */}
          <button
            className="md:hidden"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          >
            {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>

        {/* Mobile Menu */}
        {mobileMenuOpen && (
          <div className="md:hidden pb-4 border-t border-gray-100">
            {['Services', 'Partners', 'About', 'Contact'].map((item) => (
              <a
                key={item}
                href={`#${item.toLowerCase()}`}
                className="block py-2 text-gray-700 hover:text-blue-600 font-medium"
              >
                {item}
              </a>
            ))}
            <button className="w-full mt-4 bg-gradient-to-r from-blue-600 to-blue-700 text-white py-2 rounded-lg font-medium">
              Partner With Us
            </button>
          </div>
        )}
      </div>
    </nav>
  );
};

// Hero Section
const HeroSection = () => {
  const [ref, isVisible] = useScrollAnimation();

  return (
    <section className="pt-32 pb-20 px-4 sm:px-6 lg:px-8 bg-gradient-to-br from-blue-50 via-white to-blue-50 min-h-screen flex items-center relative overflow-hidden">
      {/* Animated background elements */}
      <div className="absolute top-20 right-10 w-72 h-72 bg-blue-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>
      <div className="absolute -bottom-8 left-20 w-72 h-72 bg-orange-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse" style={{ animationDelay: '2s' }}></div>

      <div className="max-w-7xl mx-auto relative z-10">
        <div className="grid md:grid-cols-2 gap-12 items-center">
          <div
            ref={ref}
            className={`space-y-6 transition-all duration-1000 ${isVisible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'}`}
          >
            <h1 className="text-5xl md:text-6xl font-bold bg-gradient-to-r from-blue-900 via-blue-700 to-orange-600 bg-clip-text text-transparent leading-tight">
              Your Trusted Business & Distribution Partner
            </h1>
            <p className="text-xl text-gray-600 leading-relaxed">
              Connect with 9 specialized service divisions and 9+ premium partner brands. Scale your business through our comprehensive ecosystem.
            </p>
            <div className="flex flex-col sm:flex-row gap-4">
              <button className="bg-gradient-to-r from-blue-600 to-blue-700 text-white px-8 py-4 rounded-lg hover:shadow-xl transition-all duration-300 font-bold flex items-center justify-center space-x-2 group">
                <span>Explore Services</span>
                <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" />
              </button>
              <button className="border-2 border-blue-600 text-blue-600 px-8 py-4 rounded-lg hover:bg-blue-50 transition-all duration-300 font-bold">
                Become a Partner
              </button>
            </div>
            <div className="flex gap-8 pt-4">
              <div>
                <p className="text-3xl font-bold text-blue-900">9+</p>
                <p className="text-gray-600">Partner Brands</p>
              </div>
              <div>
                <p className="text-3xl font-bold text-blue-900">9</p>
                <p className="text-gray-600">Service Divisions</p>
              </div>
            </div>
          </div>

          <div
            ref={ref}
            className={`relative transition-all duration-1000 delay-200 ${isVisible ? 'opacity-100 scale-100' : 'opacity-0 scale-95'}`}
          >
            <div className="relative w-full aspect-square rounded-2xl bg-gradient-to-br from-blue-600 to-blue-800 overflow-hidden">
              <div className="absolute inset-0 opacity-20">
                <svg className="w-full h-full" viewBox="0 0 100 100">
                  <defs>
                    <pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse">
                      <path d="M 10 0 L 0 0 0 10" fill="none" stroke="white" strokeWidth="0.5" />
                    </pattern>
                  </defs>
                  <rect width="100" height="100" fill="url(#grid)" />
                </svg>
              </div>
              <div className="relative h-full flex items-center justify-center">
                <div className="text-center text-white">
                  <p className="text-6xl font-bold mb-2">📊</p>
                  <p className="text-xl font-semibold">Integrated B2B<br/>Ecosystem</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

// Partners Showcase
const PartnersSection = () => {
  const partners = [
    { name: 'Turtlemint Pro', category: 'Insurance' },
    { name: 'Vyapaar', category: 'Accounting' },
    { name: 'Healthplex', category: 'Healthcare' },
    { name: 'Basundhara', category: 'Medical' },
    { name: 'Minati Perfumes', category: 'Beauty' },
    { name: 'TMYK', category: 'Education' },
    { name: 'Amazon', category: 'E-commerce' },
    { name: 'Flipkart', category: 'E-commerce' },
    { name: 'WE-NETWORK', category: 'Network' },
  ];

  const [ref, isVisible] = useScrollAnimation();

  return (
    <section className="py-20 px-4 sm:px-6 lg:px-8 bg-white">
      <div className="max-w-7xl mx-auto">
        <div
          ref={ref}
          className={`text-center mb-16 transition-all duration-1000 ${isVisible ? 'opacity-100' : 'opacity-0'}`}
        >
          <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
            Authorized Partners & Affiliations
          </h2>
          <p className="text-xl text-gray-600">
            Trusted by 9+ global and national brands
          </p>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-9 gap-4">
          {partners.map((partner, idx) => (
            <div
              key={idx}
              className={`bg-gray-50 p-6 rounded-xl hover:shadow-lg hover:bg-white transition-all duration-300 cursor-pointer transform hover:-translate-y-2 ${isVisible ? 'opacity-100' : 'opacity-0'}`}
              style={{ transitionDelay: `${idx * 50}ms` }}
            >
              <div className="w-12 h-12 bg-gradient-to-br from-blue-600 to-orange-600 rounded-lg mb-4 flex items-center justify-center text-white font-bold text-lg">
                {partner.name[0]}
              </div>
              <p className="font-bold text-gray-900 text-sm">{partner.name}</p>
              <p className="text-xs text-gray-500 mt-1">{partner.category}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

// Services Section
const ServicesSection = () => {
  const services = [
    {
      icon: '💼',
      title: 'WeHire',
      subtitle: 'Placement Consultant Services',
      description: 'Recruitment solutions, talent matching, and career consulting.',
    },
    {
      icon: '📱',
      title: 'DigiLakes',
      subtitle: 'Digital Marketing Services',
      description: 'SEO, content marketing, social media, email campaigns & analytics.',
    },
    {
      icon: '⚙️',
      title: 'Pegases',
      subtitle: 'Tech Development & Services',
      description: 'Custom app development, web solutions, IT consultation & support.',
    },
    {
      icon: '🎨',
      title: 'Crackhead',
      subtitle: 'Creative Services',
      description: 'Content writing, presentations, logo design, brand design & graphics.',
    },
    {
      icon: '💰',
      title: 'FinRush',
      subtitle: 'Financial Products Affiliate',
      description: 'Insurance, investments, financial consulting, and loan solutions.',
    },
    {
      icon: '💻',
      title: 'SASY',
      subtitle: 'Software Products Affiliate',
      description: 'Business software, accounting tools, project management & cloud solutions.',
    },
    {
      icon: '📦',
      title: 'Demihume',
      subtitle: 'Private Labeled Products',
      description: 'Private label manufacturing, product marketing, delivery & QC.',
    },
    {
      icon: '⚕️',
      title: 'MedicVue',
      subtitle: 'Medical Products Trading',
      description: 'Healthcare equipment, medical supplies, pharmaceuticals & devices.',
    },
    {
      icon: '🏋️',
      title: 'AlphaGenZ',
      subtitle: 'Healthy Food & Gym Products',
      description: 'Fitness supplements, organic foods, gym equipment & wellness coaching.',
    },
  ];

  const [ref, isVisible] = useScrollAnimation();

  return (
    <section id="services" className="py-20 px-4 sm:px-6 lg:px-8 bg-gradient-to-br from-gray-50 to-white">
      <div className="max-w-7xl mx-auto">
        <div
          ref={ref}
          className={`text-center mb-16 transition-all duration-1000 ${isVisible ? 'opacity-100' : 'opacity-0'}`}
        >
          <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
            Our Service Divisions
          </h2>
          <p className="text-xl text-gray-600">
            9 specialized verticals for comprehensive business solutions
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-6">
          {services.map((service, idx) => (
            <div
              key={idx}
              ref={ref}
              className={`bg-white p-8 rounded-2xl border border-gray-100 hover:border-blue-200 hover:shadow-xl transition-all duration-300 group cursor-pointer transform hover:-translate-y-1 ${isVisible ? 'opacity-100' : 'opacity-0'}`}
              style={{ transitionDelay: `${idx * 100}ms` }}
            >
              <div className="text-5xl mb-4 group-hover:scale-110 transition-transform duration-300">
                {service.icon}
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-2">{service.title}</h3>
              <p className="text-orange-600 font-semibold mb-3">{service.subtitle}</p>
              <p className="text-gray-600 mb-6">{service.description}</p>
              <button className="text-blue-600 font-semibold flex items-center space-x-2 group/btn">
                <span>Learn More</span>
                <ChevronRight size={18} className="group-hover/btn:translate-x-1 transition-transform" />
              </button>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

// Why Partner Section
const WhyPartnerSection = () => {
  const features = [
    { title: 'Diverse Service Portfolio', description: 'Access 9 specialized service divisions in one ecosystem' },
    { title: 'Proven Track Record', description: 'Trusted by 9+ global and national brands' },
    { title: 'Growth Partnership Program', description: 'Flexible terms and dedicated account management' },
    { title: 'Marketing Support', description: 'Co-marketing opportunities and sales support' },
    { title: 'Real-time Dashboard', description: 'Monitor performance with advanced analytics tools' },
    { title: 'Training & Onboarding', description: 'Comprehensive support programs for success' },
  ];

  const [ref, isVisible] = useScrollAnimation();

  return (
    <section className="py-20 px-4 sm:px-6 lg:px-8 bg-white">
      <div className="max-w-7xl mx-auto">
        <div className="grid md:grid-cols-2 gap-12 items-center">
          <div
            ref={ref}
            className={`space-y-6 transition-all duration-1000 ${isVisible ? 'opacity-100 translate-x-0' : 'opacity-0 -translate-x-10'}`}
          >
            <h2 className="text-4xl md:text-5xl font-bold text-gray-900">
              Why Choose VANTAIR
            </h2>
            <p className="text-xl text-gray-600">
              Join a network of successful partners and scale your business with our integrated ecosystem.
            </p>
            <div className="space-y-4">
              {features.slice(0, 3).map((feature, idx) => (
                <div key={idx} className="flex gap-4">
                  <CheckCircle2 className="text-orange-600 flex-shrink-0 mt-1" size={24} />
                  <div>
                    <p className="font-bold text-gray-900">{feature.title}</p>
                    <p className="text-gray-600">{feature.description}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div
            ref={ref}
            className={`grid grid-cols-2 gap-4 transition-all duration-1000 delay-200 ${isVisible ? 'opacity-100 translate-x-0' : 'opacity-0 translate-x-10'}`}
          >
            {features.slice(3).map((feature, idx) => (
              <div key={idx} className="bg-gradient-to-br from-blue-50 to-orange-50 p-6 rounded-xl">
                <p className="font-bold text-gray-900 mb-2">{feature.title}</p>
                <p className="text-sm text-gray-600">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
};

// Partnership CTA Section
const PartnershipCTASection = () => {
  const [ref, isVisible] = useScrollAnimation();

  return (
    <section className="py-20 px-4 sm:px-6 lg:px-8 bg-gradient-to-r from-blue-900 via-blue-800 to-blue-900 relative overflow-hidden">
      {/* Animated background */}
      <div className="absolute inset-0 opacity-10">
        <svg className="w-full h-full" viewBox="0 0 100 100">
          <defs>
            <pattern id="dots" width="20" height="20" patternUnits="userSpaceOnUse">
              <circle cx="10" cy="10" r="1" fill="white" />
            </pattern>
          </defs>
          <rect width="100" height="100" fill="url(#dots)" />
        </svg>
      </div>

      <div
        ref={ref}
        className={`max-w-4xl mx-auto text-center relative z-10 transition-all duration-1000 ${isVisible ? 'opacity-100 scale-100' : 'opacity-0 scale-95'}`}
      >
        <h2 className="text-4xl md:text-5xl font-bold text-white mb-6">
          Ready to Grow Your Business?
        </h2>
        <p className="text-xl text-blue-100 mb-8 max-w-2xl mx-auto">
          Join 9+ brands distributing through VANTAIR. Explore partnership opportunities and scale your business with our proven ecosystem.
        </p>
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <button className="bg-white text-blue-900 px-8 py-4 rounded-lg hover:shadow-xl transition-all font-bold text-lg">
            Explore Opportunities
          </button>
          <button className="border-2 border-white text-white px-8 py-4 rounded-lg hover:bg-white/10 transition-all font-bold text-lg">
            Schedule a Consultation
          </button>
        </div>
      </div>
    </section>
  );
};

// Contact Section
const ContactSection = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    company: '',
    message: '',
  });
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    // Here you would send data to backend API
    console.log('Form submitted:', formData);
    setSubmitted(true);
    setTimeout(() => {
      setFormData({ name: '', email: '', company: '', message: '' });
      setSubmitted(false);
    }, 3000);
  };

  const [ref, isVisible] = useScrollAnimation();

  return (
    <section id="contact" className="py-20 px-4 sm:px-6 lg:px-8 bg-white">
      <div className="max-w-7xl mx-auto">
        <div className="text-center mb-16">
          <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">Contact Us</h2>
          <p className="text-xl text-gray-600">Get in touch with our partnership team</p>
        </div>

        <div className="grid md:grid-cols-2 gap-12">
          {/* Contact Info */}
          <div
            ref={ref}
            className={`space-y-8 transition-all duration-1000 ${isVisible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'}`}
          >
            <div className="flex gap-6">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center flex-shrink-0">
                <Phone className="text-blue-600" size={24} />
              </div>
              <div>
                <h3 className="font-bold text-gray-900 mb-1">Phone</h3>
                <p className="text-gray-600">+91 XXXXX XXXXX</p>
              </div>
            </div>

            <div className="flex gap-6">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center flex-shrink-0">
                <Mail className="text-blue-600" size={24} />
              </div>
              <div>
                <h3 className="font-bold text-gray-900 mb-1">Email</h3>
                <p className="text-gray-600">partnership@vantair.com</p>
              </div>
            </div>

            <div className="flex gap-6">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center flex-shrink-0">
                <MapPin className="text-blue-600" size={24} />
              </div>
              <div>
                <h3 className="font-bold text-gray-900 mb-1">Address</h3>
                <p className="text-gray-600">Your Company Address<br/>City, State, Country</p>
              </div>
            </div>

            <div className="pt-8 border-t border-gray-200">
              <h3 className="font-bold text-gray-900 mb-4">Follow Us</h3>
              <div className="flex gap-4">
                {[Linkedin, Twitter, Facebook].map((Icon, idx) => (
                  <button
                    key={idx}
                    className="w-12 h-12 bg-gray-100 rounded-lg hover:bg-blue-600 hover:text-white transition-all duration-300 flex items-center justify-center"
                  >
                    <Icon size={20} />
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Contact Form */}
          <div
            ref={ref}
            className={`transition-all duration-1000 delay-200 ${isVisible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-10'}`}
          >
            <form onSubmit={handleSubmit} className="space-y-6 bg-gradient-to-br from-gray-50 to-white p-8 rounded-2xl border border-gray-100">
              {submitted && (
                <div className="bg-green-50 border border-green-200 text-green-800 p-4 rounded-lg">
                  ✓ Message sent successfully! We'll be in touch soon.
                </div>
              )}

              <div>
                <label className="block text-sm font-bold text-gray-900 mb-2">Full Name</label>
                <input
                  type="text"
                  required
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full px-4 py-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                  placeholder="Your name"
                />
              </div>

              <div>
                <label className="block text-sm font-bold text-gray-900 mb-2">Email</label>
                <input
                  type="email"
                  required
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  className="w-full px-4 py-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                  placeholder="your@email.com"
                />
              </div>

              <div>
                <label className="block text-sm font-bold text-gray-900 mb-2">Company</label>
                <input
                  type="text"
                  required
                  value={formData.company}
                  onChange={(e) => setFormData({ ...formData, company: e.target.value })}
                  className="w-full px-4 py-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                  placeholder="Your company"
                />
              </div>

              <div>
                <label className="block text-sm font-bold text-gray-900 mb-2">Message</label>
                <textarea
                  required
                  rows="4"
                  value={formData.message}
                  onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                  className="w-full px-4 py-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all resize-none"
                  placeholder="Tell us about your partnership interests..."
                ></textarea>
              </div>

              <button
                type="submit"
                className="w-full bg-gradient-to-r from-blue-600 to-blue-700 text-white font-bold py-3 rounded-lg hover:shadow-lg transition-all duration-300"
              >
                Send Message
              </button>
            </form>
          </div>
        </div>
      </div>
    </section>
  );
};

// Footer
const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-gray-900 text-gray-300 py-16 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        <div className="grid md:grid-cols-4 gap-8 mb-8">
          <div>
            <div className="flex items-center space-x-2 mb-4">
              <div className="w-8 h-8 bg-gradient-to-br from-blue-600 to-blue-800 rounded-lg flex items-center justify-center text-white font-bold">V</div>
              <span className="text-white font-bold">VANTAIR</span>
            </div>
            <p className="text-sm">Your trusted business & distribution partner</p>
          </div>

          <div>
            <h4 className="font-bold text-white mb-4">Quick Links</h4>
            <ul className="space-y-2 text-sm">
              {['Home', 'Services', 'Partners', 'About', 'Contact'].map((link) => (
                <li key={link}>
                  <a href="#" className="hover:text-white transition-colors">{link}</a>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h4 className="font-bold text-white mb-4">Services</h4>
            <ul className="space-y-2 text-sm">
              {['WeHire', 'DigiLakes', 'Pegases', 'Crackhead', 'FinRush'].map((service) => (
                <li key={service}>
                  <a href="#" className="hover:text-white transition-colors">{service}</a>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h4 className="font-bold text-white mb-4">Connect With Us</h4>
            <div className="flex gap-3 mb-4">
              {[Linkedin, Twitter, Facebook].map((Icon, idx) => (
                <button key={idx} className="w-10 h-10 bg-gray-800 hover:bg-blue-600 rounded-lg flex items-center justify-center transition-colors">
                  <Icon size={18} />
                </button>
              ))}
            </div>
            <p className="text-sm">newsletter@vantair.com</p>
          </div>
        </div>

        <div className="border-t border-gray-800 pt-8 flex flex-col md:flex-row justify-between items-center">
          <p className="text-sm">© {currentYear} VANTAIR. All rights reserved.</p>
          <div className="flex gap-6 text-sm mt-4 md:mt-0">
            <a href="#" className="hover:text-white transition-colors">Privacy Policy</a>
            <a href="#" className="hover:text-white transition-colors">Terms of Service</a>
            <a href="#" className="hover:text-white transition-colors">Disclaimer</a>
          </div>
        </div>
      </div>
    </footer>
  );
};

// Main App Component
export default function VantairWebsite() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <div className="min-h-screen bg-white font-sans">
      <Navigation mobileMenuOpen={mobileMenuOpen} setMobileMenuOpen={setMobileMenuOpen} />
      <HeroSection />
      <PartnersSection />
      <ServicesSection />
      <WhyPartnerSection />
      <PartnershipCTASection />
      <ContactSection />
      <Footer />
    </div>
  );
}