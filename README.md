# JobHunter – Fullstack Recruitment Platform

JobHunter is a comprehensive recruitment platform consisting of a **React** frontend and a **Spring Boot** backend. The system provides seamless recruitment workflows such as job postings, company catalogs, candidate resumes management, and role-based access control.

## 📂 Project Structure

This monorepo contains the following core components:

- **`jobhunter-frontend/`**: The web application built with React 18, Vite, and Ant Design.
- **`jobhunter-spring/`**: The RESTful backend API built with Java 21, Spring Boot 3, and MySQL. Follows a stateless architecture secured by Spring Security and JWT.
- **`build-docker/`**: The deployment configuration containing `docker-compose.yml` and Nginx configurations to effortlessly run the entire stack.

---

## ✨ Key Features

- **Authentication & Authorization**: Stateless JWT authentication, Refresh Tokens via HTTP-only cookies, and dynamic Role-Based Access Control (RBAC).
- **Core Modules**: Full CRUD operations for Users, Companies, Jobs, Skills, and Resumes.
- **Advanced Filtering**: Dynamic queries using JPA Specifications and Spring Filter.
- **File Management**: Uploading and downloading CVs/Logos with automated size & extension validations.
- **Automated Tasks**: Scheduled Cron Jobs for automated email notifications using Thymeleaf templates.

---

## 🚀 Getting Started

You can run this project either using **Docker (Recommended for easiest setup)** or **Locally for Development**.

### Method 1: Running with Docker (Production-ready)

This is the quickest way to get the database, backend API, and frontend running together behind an Nginx reverse proxy.

**Prerequisites:** Docker & Docker Compose installed.

1. **Configure Environment Variables:**
   Create a `.env` file in the `build-docker/` directory and configure your secrets (e.g., Database Password):
   ```env
   DB_PASSWORD=your_secure_password
   ```
2. **Build and Start:**
   Navigate to the docker directory and spin up the containers.
   ```bash
   cd build-docker
   docker compose -p jobhunter-app up -d --build
   ```
3. **Access the application:**
   - **Frontend UI**: `http://localhost`
   - **Backend API**: `http://localhost:8080/api` (also proxied via Nginx at `http://localhost/api`)

### Method 2: Running Locally (For Development)

**Prerequisites:** Java 21, Node.js 18+, MySQL 8.

**1. Database Setup:**
- Create a MySQL database named `jobhunter`.
- Ensure your local `jobhunter-spring/src/main/resources/application.properties` pointing to your local db.

**2. Start Backend:**
```bash
cd jobhunter-spring
./gradlew bootRun
```

**3. Start Frontend:**
```bash
cd jobhunter-frontend
npm install
npm run dev
```

---

## 🛠️ Tech Stack

**Frontend**
- React 18, Vite
- Ant Design, Redux Toolkit
- React Router DOM, Axios

**Backend**
- Java 21, Spring Boot 3.x
- Spring Security, JWT, OAuth2
- Spring Data JPA, Hibernate, MySQL

**DevOps**
- Docker & Docker Compose
- Nginx (Reverse Proxy)
- Swagger (OpenAPI 3) for API Documentation
