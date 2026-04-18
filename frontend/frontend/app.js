const BACKEND_URL = 'https://verichain-backend.onrender.com';

const MOCK_DATA = [
  {
    trackingId: 'TRK-A1B2C3D4',
    origin: 'Mumbai, Maharashtra',
    destination: 'Bangalore, Karnataka',
    senderName: 'Rahul Sharma',
    receiverName: 'Priya Singh',
    weightKg: 2.5, distanceKm: 980,
    currentStatus: 'IN_TRANSIT',
    aiDelayRisk: 'HIGH', aiEstimatedDelayHours: 12,
    aiConfidenceScore: 0.94, aiRecommendedAction: 'REROUTE',
    blockchainTxHash: '0x7f8a9b3c2d1e4f5a6b7c8d9e0f1a2b3c',
    loggedOnChain: true,
    expectedDelivery: '2026-04-20T14:30:00',
    createdAt: '2026-04-18T09:00:00'
  },
  {
    trackingId: 'TRK-9876XYZA',
    origin: 'Delhi, NCR',
    destination: 'Chennai, Tamil Nadu',
    senderName: 'Ankit Verma',
    receiverName: 'Meena Pillai',
    weightKg: 5.0, distanceKm: 2180,
    currentStatus: 'PENDING',
    aiDelayRisk: 'LOW', aiEstimatedDelayHours: 0,
    aiConfidenceScore: 0.91, aiRecommendedAction: 'PROCEED',
    blockchainTxHash: '0xab12cd34ef56gh78ij90kl12mn34op56',
    loggedOnChain: true,
    expectedDelivery: '2026-04-22T10:00:00',
    createdAt: '2026-04-18T10:00:00'
  },
  {
    trackingId: 'TRK-ZX99AB12',
    origin: 'Hyderabad, Telangana',
    destination: 'Pune, Maharashtra',
    senderName: 'Sunita Rao',
    receiverName: 'Karan Mehta',
    weightKg: 1.2, distanceKm: 560,
    currentStatus: 'DELIVERED',
    aiDelayRisk: 'LOW', aiEstimatedDelayHours: 0,
    aiConfidenceScore: 0.97, aiRecommendedAction: 'PROCEED',
    blockchainTxHash: '0x1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d',
    loggedOnChain: true,
    expectedDelivery: '2026-04-17T16:00:00',
    createdAt: '2026-04-16T08:00:00'
  }
];

document.addEventListener('DOMContentLoaded', () => {
  lucide.createIcons();
  fetchShipments();
  setupEasterEgg();
});

async function fetchShipments() {
  const grid      = document.getElementById('shipment-grid');
  const loader    = document.getElementById('loading');
  const errBanner = document.getElementById('error-banner');

  try {
    const response = await fetch(`${BACKEND_URL}/api/shipments`, {
      headers: { 'Content-Type': 'application/json' },
      signal: AbortSignal.timeout(8000)
    });
    if (!response.ok) throw new Error('Backend error');
    const shipments = await response.json();
    loader.style.display = 'none';
    if (!shipments || shipments.length === 0) {
      renderShipments(MOCK_DATA, grid);
      updateStats(MOCK_DATA);
      return;
    }
    renderShipments(shipments, grid);
    updateStats(shipments);
  } catch (err) {
    console.warn('Backend offline — loading demo data');
    loader.style.display = 'none';
    errBanner.style.display = 'flex';
    renderShipments(MOCK_DATA, grid);
    updateStats(MOCK_DATA);
  }
}

function renderShipments(shipments, container) {
  container.innerHTML = '';
  shipments.forEach(shipment => {
    const statusClass = shipment.currentStatus.toLowerCase();
    const risk        = (shipment.aiDelayRisk || 'LOW').toLowerCase();
    const borderColor = risk === 'high' ? '#ef4444' : risk === 'medium' ? '#eab308' : '#10b981';
    const eta     = shipment.expectedDelivery
      ? new Date(shipment.expectedDelivery).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })
      : 'N/A';
    const created = shipment.createdAt
      ? new Date(shipment.createdAt).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })
      : 'N/A';
    const txHash = shipment.blockchainTxHash
      ? `<span class="hash-string">${shipment.blockchainTxHash.substring(0,20)}...${shipment.blockchainTxHash.slice(-6)}</span>`
      : `<span class="not-logged">Not yet logged on-chain</span>`;
    const onChainStatus = shipment.loggedOnChain
      ? '<span style="color:#10b981;font-size:0.75rem;">✓ Verified on Sepolia Testnet</span>'
      : '<span style="color:#94a3b8;font-size:0.75rem;">Pending verification</span>';

    const card = document.createElement('div');
    card.className = 'shipment-card';
    card.innerHTML = `
      <div class="card-header">
        <h2><i data-lucide="package" class="icon"></i>${shipment.trackingId}</h2>
        <span class="status-badge ${statusClass}">${shipment.currentStatus.replace('_',' ')}</span>
      </div>
      <div class="route-info">
        <div class="route-arrow">
          <span>${shipment.origin}</span>
          <span class="arrow">→</span>
          <span>${shipment.destination}</span>
        </div>
        <p><strong>Sender:</strong> ${shipment.senderName} &nbsp;|&nbsp; <strong>Receiver:</strong> ${shipment.receiverName}</p>
        <p><strong>Weight:</strong> ${shipment.weightKg} kg &nbsp;|&nbsp; <strong>Distance:</strong> ${shipment.distanceKm} km</p>
        <p><strong>ETA:</strong> ${eta}</p>
        <p><strong>Created:</strong> ${created}</p>
      </div>
      <hr />
      <div class="ai-blockchain-panel">
        <div class="ai-prediction" style="border-left-color:${borderColor}">
          <h3><i data-lucide="brain" class="icon-small" style="color:${borderColor}"></i>AI Delay Prediction</h3>
          <div class="ai-value ${risk}">
            ${risk.toUpperCase()} RISK${shipment.aiEstimatedDelayHours > 0 ? ` — ${shipment.aiEstimatedDelayHours}h delay` : ''}
          </div>
          <div class="ai-meta">
            Confidence: ${shipment.aiConfidenceScore ? (shipment.aiConfidenceScore*100).toFixed(1)+'%' : 'N/A'}
            &nbsp;|&nbsp; Action: ${shipment.aiRecommendedAction || 'PROCEED'}
          </div>
        </div>
        <div class="blockchain-ledger">
          <h3><i data-lucide="shield-check" class="icon-small" style="color:#10b981"></i>Blockchain Ledger</h3>
          ${onChainStatus}
          ${txHash}
        </div>
      </div>
    `;
    container.appendChild(card);
  });
  lucide.createIcons();
}

function updateStats(shipments) {
  document.getElementById('stat-total').textContent   = shipments.length;
  document.getElementById('stat-transit').textContent = shipments.filter(s => s.currentStatus === 'IN_TRANSIT').length;
  document.getElementById('stat-chain').textContent   = shipments.filter(s => s.loggedOnChain).length;
  document.getElementById('stat-risk').textContent    = shipments.filter(s => s.aiDelayRisk === 'HIGH').length;
}

function setupEasterEgg() {
  let keySequence = [];
  document.addEventListener('keydown', (e) => {
    keySequence.push(e.key.toLowerCase());
    if (keySequence.length > 4) keySequence.shift();
    if (keySequence.join('') === 'hire') {
      document.body.innerHTML = `
        <div class="terminal-screen">
          <h1>&gt; Root Access Granted.</h1>
          <p>&gt; Initializing Developer Profile...</p>
          <p>&gt; Name: <strong>Kunal Verma</strong></p>
          <p>&gt; Stack: Java · Spring Boot · Python · Web3j</p>
          <p>&gt; Architecture: Microservices · JWT · BCrypt · Blockchain</p>
          <p>&gt; Deployed: Render + Netlify</p>
          <p>&gt; GitHub: <strong>github.com/Ku502</strong></p>
          <p class="highlight">&gt; Status: AVAILABLE FOR HIRE — 2026</p>
          <p class="reboot">&gt; Press F5 to reboot system.</p>
        </div>
      `;
    }
  });
}
