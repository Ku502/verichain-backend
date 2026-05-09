# VeriChain AI — Supply Chain Tracker

A full-stack supply chain tracking system that predicts delivery delays using AI and logs shipment records permanently on the Ethereum blockchain.

Built this as a final year project to demonstrate enterprise-level backend architecture using microservices.

## What it does

- Create and track shipments with a unique tracking ID
- Automatically predicts delivery delay risk (LOW / MEDIUM / HIGH) based on weather and distance using a Python AI service
- Logs every shipment checkpoint permanently on the Ethereum Sepolia blockchain using Web3j — tamper-proof audit trail
- JWT based authentication with role based access (Admin, Manager, Driver, Viewer)
- Live dashboard to visualize shipments, AI predictions and blockchain status

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.2, Spring Security, JWT, BCrypt
- **Database:** H2 (development), PostgreSQL (production), Spring Data JPA, Hibernate
- **AI Microservice:** Python 3, FastAPI
- **Blockchain:** Solidity, Web3j, Ethereum Sepolia Testnet
- **Frontend:** HTML, CSS, Vanilla javascript.
- **Deployment:** Docker on Render (backend), Netlify (frontend).

## Live Links

- Frontend: https://verichainai.netlify.app
- Backend API: https://verichain-backend.onrender.com

