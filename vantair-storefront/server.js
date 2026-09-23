// ============================================================
// VANTAIR — FRONTEND SERVER
// Zero-dependency static file server that also reverse-proxies
// /api/* (and Swagger paths) to the Spring Boot backend.
//
//   node server.js                 # serves on :5500, proxies to :8080
//   PORT=3000 API=http://host:8080 node server.js
//
// Serving the site and the API under the same origin means the
// browser makes same-origin calls — no CORS needed.
// ============================================================

const http = require('http');
const https = require('https');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || 5500;
const API_TARGET = process.env.API || 'http://localhost:8080';
const ROOT = __dirname;

// Paths that are proxied to the backend rather than served from disk.
const PROXY_PREFIXES = ['/api', '/v3/api-docs', '/swagger-ui', '/swagger-ui.html'];

const MIME = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.png': 'image/png',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.txt': 'text/plain; charset=utf-8',
};

function isProxied(url) {
  return PROXY_PREFIXES.some(p => url === p || url.startsWith(p + '/') || url.startsWith(p + '?'));
}

function proxy(clientReq, clientRes) {
  const target = new URL(API_TARGET);
  const isHttps = target.protocol === 'https:';
  const options = {
    hostname: target.hostname,
    port: target.port || (isHttps ? 443 : 80),
    path: clientReq.url,
    method: clientReq.method,
    headers: { ...clientReq.headers, host: target.host },
  };

  const proxyReq = (isHttps ? https : http).request(options, proxyRes => {
    clientRes.writeHead(proxyRes.statusCode, proxyRes.headers);
    proxyRes.pipe(clientRes, { end: true });
  });

  proxyReq.on('error', err => {
    clientRes.writeHead(502, { 'Content-Type': 'application/json' });
    clientRes.end(JSON.stringify({
      error: 'Bad Gateway',
      message: `Backend unreachable at ${API_TARGET}. Is the Spring Boot app running? (${err.code})`,
    }));
  });

  clientReq.pipe(proxyReq, { end: true });
}

function serveStatic(req, res) {
  // Strip query string, default to index.html, prevent path traversal.
  let urlPath = decodeURIComponent(req.url.split('?')[0]);
  if (urlPath === '/') urlPath = '/index.html';
  const filePath = path.normalize(path.join(ROOT, urlPath));
  if (!filePath.startsWith(ROOT)) {
    res.writeHead(403); res.end('Forbidden'); return;
  }

  fs.readFile(filePath, (err, data) => {
    if (err) {
      // Fall back to the custom 404 page if present.
      fs.readFile(path.join(ROOT, '404.html'), (e2, page) => {
        res.writeHead(404, { 'Content-Type': 'text/html; charset=utf-8' });
        res.end(e2 ? 'Not Found' : page);
      });
      return;
    }
    const ext = path.extname(filePath).toLowerCase();
    res.writeHead(200, { 'Content-Type': MIME[ext] || 'application/octet-stream' });
    res.end(data);
  });
}

const server = http.createServer((req, res) => {
  if (isProxied(req.url)) proxy(req, res);
  else serveStatic(req, res);
});

server.listen(PORT, () => {
  console.log(`\n  VANTAIR storefront → http://localhost:${PORT}`);
  console.log(`  Proxying /api → ${API_TARGET}\n`);
});
