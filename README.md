<h3>Table of Contents</h3>
<ul>
    <li><a href="#introduction">Introduction</a></li>
    <li><a href="#architecture">Architecture</a></li>
    <li><a href="#features">Key Features</a></li>
    <li><a href="#tech-stack">Tech Stack</a></li>
    <li><a href="#requirements">Requirements</a></li>
    <li><a href="#installation-and-setup">Installation and Setup</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#api-overview">API Overview</a></li>
    <li><a href="#status">Current Status</a></li>
</ul>

<h3 id="introduction">Introduction</h3>
<p>
   <b>Super Tunes</b> is a microservices backend project for a music platform.
   The system is split into independent Spring Boot services for users, songs, and playlists,
   with service discovery and API gateway support.
</p>
<ul>
   <li>Each service owns its own logic and database schema.</li>
   <li>Services are discoverable through Eureka and can be routed through API Gateway.</li>
</ul>

<h3 id="architecture">Architecture</h3>
<ul>
   <li><b>discovery-server</b>: Eureka service registry.</li>
   <li><b>api-gateway</b>: single entry point for client requests.</li>
   <li><b>user-service</b>: user CRUD and validation flow.</li>
   <li><b>song-service</b>: song CRUD flow.</li>
   <li><b>playlist-service</b>: playlist CRUD flow.</li>
</ul>

<h3 id="features">Key Features</h3>
<ul>
   <li>Microservices-based backend design.</li>
   <li>Layered code structure: <code>controller</code>, <code>service</code>, <code>repository</code>, <code>entity</code>.</li>
   <li>Spring Data JPA integration with PostgreSQL.</li>
   <li>Request validation and centralized exception handling (user-service).</li>
   <li>Maven multi-module root setup for full-project build.</li>
</ul>

<h3 id="tech-stack">Tech Stack</h3>
<ul>
   <li>Java 17+</li>
   <li>Spring Boot</li>
   <li>Spring Cloud (Eureka)</li>
   <li>Spring Data JPA (Hibernate)</li>
   <li>PostgreSQL</li>
   <li>Maven</li>
</ul>

<h3 id="requirements">Requirements</h3>
<ul>
   <li>Java 17+ installed and configured.</li>
   <li>Maven installed (or use each module's <code>./mvnw</code>).</li>
   <li>PostgreSQL running locally.</li>
   <li>Databases created:
      <ul>
         <li><code>users_db</code></li>
         <li><code>songs_db</code></li>
         <li><code>playlists_db</code></li>
      </ul>
   </li>
</ul>

<h3 id="installation-and-setup">Installation and Setup</h3>
<ul>
   <li>Clone repository.</li>
   <li>Move to project root folder.</li>
   <li>Build all modules from root:</li>
</ul>

<pre><code>mvn clean compile</code></pre>

<h3 id="usage">Usage</h3>
<p><b>Recommended startup order:</b></p>
<ol>
   <li>Start <code>discovery-server</code></li>
   <li>Start <code>api-gateway</code></li>
   <li>Start <code>user-service</code>, <code>song-service</code>, <code>playlist-service</code></li>
</ol>

<p><b>Gateway test URLs:</b></p>
<ul>
   <li><code>http://localhost:8080/api/users</code></li>
   <li><code>http://localhost:8080/api/songs</code></li>
   <li><code>http://localhost:8080/api/playlists</code></li>
</ul>

<p>Run any single service:</p>
<pre><code>cd user-service
./mvnw spring-boot:run</code></pre>

<h3 id="api-overview">API Overview</h3>
<p><b>User Service</b></p>
<ul>
   <li><code>GET /api/users</code></li>
   <li><code>GET /api/users/{id}</code></li>
   <li><code>GET /api/users/email/{email}</code></li>
   <li><code>POST /api/users</code></li>
   <li><code>PUT /api/users/{id}</code></li>
   <li><code>DELETE /api/users/{id}</code></li>
</ul>

<p><b>Song Service</b></p>
<ul>
   <li><code>GET /api/songs</code></li>
   <li><code>GET /api/songs/{id}</code></li>
   <li><code>POST /api/songs</code></li>
   <li><code>PUT /api/songs/{id}</code></li>
   <li><code>DELETE /api/songs/{id}</code></li>
</ul>

<p><b>Playlist Service</b></p>
<ul>
   <li><code>GET /api/playlists</code></li>
   <li><code>GET /api/playlists/{id}</code></li>
   <li><code>POST /api/playlists</code></li>
   <li><code>PUT /api/playlists/{id}</code></li>
   <li><code>DELETE /api/playlists/{id}</code></li>
</ul>

<h3 id="status">Current Status</h3>
<ul>
   <li><b>user-service</b>: mostly complete.</li>
   <li><b>song-service</b>: basic CRUD complete.</li>
   <li><b>playlist-service</b>: basic CRUD complete.</li>
   <li><b>api-gateway</b>: routes configured and verified with all services.</li>
   <li><b>discovery-server</b>: configured and running as Eureka registry.</li>
</ul>
