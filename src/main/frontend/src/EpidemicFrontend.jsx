import React, { useRef, useState, useEffect, useCallback } from "react";

const lerp = (start, end, t) => {
  return start * (1 - t) + end * t;
};

const getOffset = (id, size) => {
  const seed = id * 12345;
  const x = Math.sin(seed) * (size * 0.5);
  const y = Math.cos(seed) * (size * 0.5);
  return { x, y };
};

function hexToPixel(col, row, size) {
  const hexWidth = 2 * size;
  const hexHeight = Math.sqrt(3) * size;
  const x = col * (hexWidth * 0.75) + 30;
  const y = row * hexHeight + (col % 2 === 1 ? hexHeight * 0.5 : 0) + 30;
  return { x, y };
}

export default function EpidemicFrontend() {
  const canvasRef = useRef(null);
  const chartRef = useRef(null);
  const socketRef = useRef(null);
  const reqRef = useRef(null);
  const isRunningRef = useRef(false);

  const [wsUrl, setWsUrl] = useState("ws://localhost:9000/ws");
  const [connected, setConnected] = useState(false);

  const [boardSize, setBoardSize] = useState({ width: 10, height: 10 });
  const [hexSize, setHexSize] = useState(24);
  const [history, setHistory] = useState([]);
  const [simParams, setSimParams] = useState({ width: 10, height: 10, population: 10, initialInfected: 1 });
  const [simulationStarted, setSimulationStarted] = useState(false);

  const animationState = useRef({
    startAgents: [],
    targetAgents: [],
    startTime: 0,
    duration: 500
  });

  const handleMessage = useCallback((data) => {
    try {
      const payload = JSON.parse(data);

      if (payload.type === "state" && Array.isArray(payload.agents)) {
        setBoardSize({ width: payload.width || 10, height: payload.height || 10 });

        const newAgents = payload.agents.map((a, index) => ({
          id: index,
          x: a.x,
          y: a.y,
          status: a.status
        }));

        const now = performance.now();
        const state = animationState.current;

        if (state.targetAgents.length === 0) {
          state.startAgents = newAgents;
          state.targetAgents = newAgents;
        } else {
          state.startAgents = state.targetAgents;
          state.targetAgents = newAgents;
        }

        state.startTime = now;

        if (isRunningRef.current) {
          const infectedCount = newAgents.filter(a => a.status === 1).length;
          setHistory(prev => {
            const newHist = [...prev, { t: Date.now(), inf: infectedCount }];
            return newHist.slice(-200);
          });
        }
      }
    } catch (e) {
      console.error(e);
    }
  }, []);

  const animate = useCallback((time) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    const dpr = window.devicePixelRatio || 1;

    const { startTime, duration, startAgents, targetAgents } = animationState.current;
    let progress = (time - startTime) / duration;
    if (progress > 1) progress = 1;

    const pixelWidth = boardSize.width * (hexSize * 1.6) + 60;
    const pixelHeight = boardSize.height * (hexSize * 1.8) + 60;

    if (canvas.width !== Math.floor(pixelWidth * dpr) || canvas.height !== Math.floor(pixelHeight * dpr)) {
      canvas.width = Math.floor(pixelWidth * dpr);
      canvas.height = Math.floor(pixelHeight * dpr);
      canvas.style.width = pixelWidth + "px";
      canvas.style.height = pixelHeight + "px";
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    }

    ctx.fillStyle = "#0f172a";
    ctx.fillRect(0, 0, pixelWidth, pixelHeight);

    targetAgents.forEach((targetAgent, index) => {
      const startAgent = startAgents[index] || targetAgent;

      const startPos = hexToPixel(startAgent.x, startAgent.y, hexSize);
      const targetPos = hexToPixel(targetAgent.x, targetAgent.y, hexSize);

      const currentX = lerp(startPos.x, targetPos.x, progress);
      const currentY = lerp(startPos.y, targetPos.y, progress);

      const offset = getOffset(targetAgent.id, hexSize);

      const finalX = currentX + offset.x;
      const finalY = currentY + offset.y;

      ctx.beginPath();
      ctx.arc(finalX, finalY, hexSize * 0.25, 0, 2 * Math.PI);

      if (targetAgent.status === 2) {
        ctx.fillStyle = "#000000";
        ctx.shadowBlur = 0;
      } else if (targetAgent.status === 1) {
        ctx.fillStyle = "#f43f5e";
        ctx.shadowColor = "#f43f5e";
        ctx.shadowBlur = 15;
      } else {
        ctx.fillStyle = "#3b82f6";
        ctx.shadowBlur = 0;
      }

      ctx.fill();
    });

    reqRef.current = requestAnimationFrame(animate);
  }, [boardSize, hexSize]);

  useEffect(() => {
    reqRef.current = requestAnimationFrame(animate);
    return () => cancelAnimationFrame(reqRef.current);
  }, [animate]);

  useEffect(() => {
    const c = chartRef.current;
    if (!c) return;
    const ctx = c.getContext("2d");
    const dpr = window.devicePixelRatio || 1;
    const W = c.clientWidth; const H = c.clientHeight;
    c.width = W * dpr; c.height = H * dpr;
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    ctx.clearRect(0, 0, W, H);
    ctx.fillStyle = "rgba(255,255,255,0.05)"; ctx.fillRect(0, 0, W, H);

    if (history.length < 2) return;
    const maxVal = Math.max(...history.map(h => h.inf), 10);
    ctx.beginPath(); ctx.strokeStyle = "#f43f5e"; ctx.lineWidth = 2;
    for (let i = 0; i < history.length; i++) {
      const point = history[i];
      const x = (i / (history.length - 1)) * W;
      const y = H - (point.inf / maxVal) * (H - 10);
      if (i === 0) ctx.moveTo(x, y); else ctx.lineTo(x, y);
    }
    ctx.stroke();
  }, [history]);

  const connect = useCallback(() => {
    if (socketRef.current) socketRef.current.close();
    try {
      const ws = new WebSocket(wsUrl);
      ws.onopen = () => setConnected(true);
      ws.onclose = () => setConnected(false);
      ws.onmessage = (ev) => handleMessage(ev.data);
      socketRef.current = ws;
    } catch (e) { setConnected(false); }
  }, [wsUrl, handleMessage]);

  useEffect(() => { return () => { if (socketRef.current) socketRef.current.close(); }; }, []);

  const sendCommand = useCallback((cmd, payload = {}) => {
    if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
      socketRef.current.send(JSON.stringify({ command: cmd, ...payload }));
    }
  }, []);

  useEffect(() => {
    if (connected && !simulationStarted) {
      const handler = setTimeout(() => {
        sendCommand("configure", simParams);
      }, 500);
      return () => clearTimeout(handler);
    }
  }, [connected, simulationStarted, simParams, sendCommand]);

  return (
    <div style={{ display: "grid", gridTemplateColumns: "300px 1fr", gridTemplateRows: "1fr", gap: "20px", padding: "20px", fontFamily: "Inter, sans-serif", background: "#0b1220", height: "100vh", boxSizing: "border-box", overflow: "hidden", color: "#e2e8f0" }}>
      <div style={{ display: "flex", flexDirection: "column", gap: "12px", overflowY: "auto", paddingRight: "8px" }}>
        <div style={{ background: "rgba(255,255,255,0.05)", padding: "16px", borderRadius: "8px" }}>
          <h3 style={{ margin: "0 0 12px 0", fontSize: "16px", color: "#94a3b8" }}>Connection</h3>
          <input value={wsUrl} onChange={e => setWsUrl(e.target.value)} style={{ width: "93%", padding: "8px", marginBottom: "8px", background: "#1e293b", border: "1px solid #334155", color: "white", borderRadius: "4px" }} />
          <button onClick={connect} disabled={connected} style={{ width: "100%", padding: "8px", borderRadius: "4px", border: "none", cursor: "pointer", background: connected ? "#10b981" : "#3b82f6", color: "white", fontWeight: "bold" }}>{connected ? "Connected" : "Connect"}</button>
        </div>
        <div style={{ background: "rgba(255,255,255,0.05)", padding: "16px", borderRadius: "8px" }}>
          <h3 style={{ margin: "0 0 12px 0", fontSize: "16px", color: "#94a3b8" }}>Control</h3>
          <div style={{ display: "flex", gap: "8px" }}>
            <button onClick={() => { 
              sendCommand("start"); 
              setSimulationStarted(true); 
              const currentAgents = animationState.current.targetAgents;
              const infectedCount = currentAgents.filter(a => a.status === 1).length;
              setHistory([{ t: Date.now(), inf: infectedCount }]);
              isRunningRef.current = true; 
            }} style={{ flex: 1, padding: "8px", borderRadius: "4px", border: "none", cursor: "pointer", background: "#19d219", color: "white", fontWeight: "bold" }}>Start</button>
            <button onClick={() => { 
              sendCommand("stop"); 
              isRunningRef.current = false; 
            }} style={{ flex: 1, padding: "8px", borderRadius: "4px", border: "none", cursor: "pointer", background: "#e0e018", color: "white", fontWeight: "bold" }}>Stop</button>
            <button onClick={() => { 
              sendCommand("reset"); 
              setSimulationStarted(false); 
              setHistory([]); 
              isRunningRef.current = false; 
            }} style={{ flex: 1, padding: "8px", borderRadius: "4px", border: "none", cursor: "pointer", background: "#d12b2b", color: "white", fontWeight: "bold" }}>Reset</button>
          </div>
        </div>
        <div style={{ background: "rgba(255,255,255,0.05)", padding: "16px", borderRadius: "8px" }}>
          <h3 style={{ margin: "0 0 12px 0", fontSize: "16px", color: "#94a3b8" }}>Settings</h3>
          <div style={{ marginBottom: "12px" }}>
            <label style={{ display: "block", marginBottom: "4px", fontSize: "12px" }}>Hex Size: {hexSize}px</label>
            <input type="range" min="6" max="48" value={hexSize} onChange={e => setHexSize(Number(e.target.value))} style={{ width: "100%" }} />
          </div>
          <div style={{ marginBottom: "12px" }}>
            <label style={{ display: "block", marginBottom: "4px", fontSize: "12px" }}>Board Width: {simParams.width}</label>
            <input type="range" min="8" max="64" value={simParams.width} disabled={simulationStarted} onChange={e => setSimParams(p => ({...p, width: Number(e.target.value)}))} style={{ width: "100%" }} />
          </div>
          <div style={{ marginBottom: "12px" }}>
            <label style={{ display: "block", marginBottom: "4px", fontSize: "12px" }}>Board Height: {simParams.height}</label>
            <input type="range" min="8" max="32" value={simParams.height} disabled={simulationStarted} onChange={e => setSimParams(p => ({...p, height: Number(e.target.value)}))} style={{ width: "100%" }} />
          </div>
          <div style={{ marginBottom: "12px" }}>
            <label style={{ display: "block", marginBottom: "4px", fontSize: "12px" }}>Population: {simParams.population}</label>
            <input type="range" min="2" max="500" value={simParams.population} disabled={simulationStarted} onChange={e => { const val = Number(e.target.value); setSimParams(p => ({...p, population: val, initialInfected: Math.min(p.initialInfected, val) })); }} style={{ width: "100%" }} />
          </div>
          <div style={{ marginBottom: "12px" }}>
            <label style={{ display: "block", marginBottom: "4px", fontSize: "12px" }}>Initial Infected: {simParams.initialInfected}</label>
            <input type="range" min="1" max={simParams.population} value={simParams.initialInfected} disabled={simulationStarted} onChange={e => setSimParams(p => ({...p, initialInfected: Number(e.target.value)}))} style={{ width: "100%" }} />
          </div>
        </div>
        <div style={{ background: "rgba(255,255,255,0.05)", padding: "16px", borderRadius: "8px" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "12px" }}>
            <h3 style={{ margin: "0", fontSize: "16px", color: "#94a3b8" }}>Statistics</h3>
            <span style={{ color: "#f43f5e", fontWeight: "bold", fontSize: "14px" }}>
              Infected: {history.length > 0 ? history[history.length - 1].inf : 0}
            </span>
          </div>
          <canvas ref={chartRef} style={{ width: "100%", height: "120px", borderRadius: "4px" }} />
        </div>
      </div>
      <div style={{ background: "#1e293b", borderRadius: "8px", overflow: "hidden", display: "flex", justifyContent: "center", alignItems: "center", boxShadow: "inset 0 0 20px rgba(0,0,0,0.5)" }}>
        <canvas ref={canvasRef} />
      </div>
    </div>
  );
}