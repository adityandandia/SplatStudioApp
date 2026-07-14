package com.example.ui.screens

object SplatHtmlTemplate {
    fun getHtml(modelUrl: String?, title: String): String {
        val modelLabel = modelUrl ?: "Demo_Gaussian_Splat_Object"
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
            <title>SplatStudio 3D Viewer</title>
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    overflow: hidden;
                    background-color: #1C1B1F;
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                }
                #canvas-container {
                    width: 100vw;
                    height: 100vh;
                    touch-action: none;
                }
                #canvas-container canvas {
                    touch-action: none;
                }
                #hud {
                    position: absolute;
                    top: 16px;
                    left: 16px;
                    color: white;
                    font-size: 12px;
                    background: rgba(28, 27, 31, 0.9); /* Translucent Obsidian */
                    padding: 10px 14px;
                    border-radius: 12px;
                    pointer-events: auto; /* ENABLE interactions so we can click the toggle */
                    border: 1px solid rgba(255,255,255,0.15);
                    box-shadow: 0 4px 20px rgba(0,0,0,0.4);
                    backdrop-filter: blur(10px);
                    z-index: 1000;
                }
                #hud h2 {
                    margin: 0 0 4px 0;
                    font-size: 14px;
                    font-weight: 600;
                    color: #D0BCFF; /* Material 3 styled heading color */
                }
                #hud p {
                    margin: 2px 0;
                    opacity: 0.8;
                    font-family: monospace;
                    font-size: 11px;
                }
                #diagnostics-toggle {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    gap: 8px;
                    margin-top: 6px;
                    padding: 4px 8px;
                    background: rgba(255,255,255,0.08);
                    border: 1px solid rgba(255,255,255,0.12);
                    border-radius: 6px;
                    cursor: pointer;
                    font-size: 10px;
                    color: #A5B4FC;
                    font-weight: 600;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                    transition: background 0.2s;
                }
                #diagnostics-toggle:active {
                    background: rgba(255,255,255,0.15);
                }
                #diagnostics-content {
                    display: none; /* Collapsed by default */
                    margin-top: 8px;
                    border-top: 1px solid rgba(255,255,255,0.1);
                    padding-top: 8px;
                }
                .control-btn {
                    position: absolute;
                    bottom: 24px;
                    left: 50%;
                    transform: translateX(-50%);
                    background: rgba(255,255,255,0.15);
                    border: 1px solid rgba(255,255,255,0.25);
                    color: white;
                    padding: 8px 16px;
                    border-radius: 20px;
                    font-size: 12px;
                    pointer-events: auto;
                    cursor: pointer;
                }
                #settings-toggle-btn {
                    position: absolute;
                    top: 16px;
                    right: 16px;
                    background: rgba(103, 80, 164, 0.85); /* Material 3 brand purple */
                    border: 1px solid rgba(255,255,255,0.2);
                    color: white;
                    width: 38px;
                    height: 38px;
                    border-radius: 50%;
                    font-size: 18px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    cursor: pointer;
                    pointer-events: auto;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.3);
                    transition: all 0.2s ease;
                    z-index: 999;
                }
                #settings-toggle-btn:active {
                    transform: scale(0.9);
                    background: rgb(103, 80, 164);
                }
                #settings-panel {
                    position: absolute;
                    top: 64px;
                    right: 16px;
                    width: 260px;
                    background: rgba(28, 27, 31, 0.92); /* Translucent obsidian */
                    border: 1px solid rgba(255,255,255,0.15);
                    border-radius: 12px;
                    padding: 14px;
                    color: white;
                    display: none; /* Collapsed by default */
                    flex-direction: column;
                    gap: 12px;
                    pointer-events: auto;
                    box-shadow: 0 8px 32px rgba(0,0,0,0.5);
                    z-index: 998;
                    font-size: 11px;
                }
                .setting-row {
                    display: flex;
                    flex-direction: column;
                    gap: 4px;
                }
                .setting-label-container {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    opacity: 0.9;
                }
                .setting-title {
                    font-weight: 600;
                    font-size: 11px;
                    color: #E8DEF8;
                }
                .setting-val {
                    font-family: monospace;
                    color: #D0BCFF;
                    font-size: 11px;
                }
                .setting-slider {
                    -webkit-appearance: none;
                    width: 100%;
                    height: 6px;
                    border-radius: 3px;
                    background: rgba(255,255,255,0.15);
                    outline: none;
                    margin: 6px 0;
                }
                .setting-slider::-webkit-slider-thumb {
                    -webkit-appearance: none;
                    appearance: none;
                    width: 16px;
                    height: 16px;
                    border-radius: 50%;
                    background: #D0BCFF;
                    cursor: pointer;
                    box-shadow: 0 2px 6px rgba(0,0,0,0.4);
                }
                .setting-slider::-webkit-slider-thumb:active {
                    background: #6750A4;
                }
                .preset-container {
                    display: flex;
                    gap: 6px;
                    margin-top: 4px;
                    border-top: 1px solid rgba(255,255,255,0.1);
                    padding-top: 8px;
                }
                .preset-btn {
                    flex: 1;
                    background: rgba(255,255,255,0.08);
                    border: 1px solid rgba(255,255,255,0.1);
                    color: #E8DEF8;
                    padding: 5px 0;
                    border-radius: 6px;
                    font-size: 9px;
                    cursor: pointer;
                    text-align: center;
                    font-weight: 500;
                    transition: all 0.15s ease;
                }
                .preset-btn:active, .preset-btn.active {
                    background: rgba(208, 188, 255, 0.2);
                    border-color: #D0BCFF;
                    color: white;
                }
            </style>
            <!-- Include Three.js and OrbitControls -->
            <script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/three@0.128.0/examples/js/controls/OrbitControls.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/three@0.128.0/examples/js/loaders/PLYLoader.js"></script>
        </head>
        <body>
            <div id="hud" onpointerdown="event.stopPropagation()" ontouchstart="event.stopPropagation()" onmousedown="event.stopPropagation()">
                <h2>$title</h2>
                <div id="diagnostics-toggle" onclick="toggleDiagnostics()">
                    <span>Diagnostics</span> <span id="diagnostics-arrow">▸</span>
                </div>
                <div id="diagnostics-content">
                    <p>Mode: 3D Point-Cloud/Splat</p>
                    <p id="stats">Vertices: 14,820</p>
                    <div id="cam-diagnostics" style="color:#A5B4FC; font-family:monospace; margin-top: 6px; border-top: 1px solid rgba(255,255,255,0.1); padding-top: 6px; font-size: 10px; line-height: 1.4;">
                        Polar: --<br>Azimuth: --<br>Distance: --
                    </div>
                    <p id="buildmark" style="color:#6EE7B7; margin-top: 6px;">Build: ${System.currentTimeMillis()}</p>
                </div>
            </div>
            <div id="settings-toggle-btn" onclick="toggleSettingsPanel()">⚙️</div>
            <div id="settings-panel">
                <div style="font-size: 13px; font-weight: 600; color: white; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 6px; margin-bottom: 4px;">
                    <span>Visual Parameters</span>
                    <span style="opacity: 0.5; font-size: 10px; cursor: pointer;" onclick="resetToDefaults()">Reset</span>
                </div>
                
                <div class="setting-row">
                    <div class="setting-label-container">
                        <span class="setting-title">Splat Scale</span>
                        <span id="scale-val" class="setting-val">1.00x</span>
                    </div>
                    <input type="range" id="scale-slider" class="setting-slider" min="0.2" max="2.5" step="0.05" value="1.0" oninput="onScaleChange(this.value)">
                </div>

                <div class="setting-row">
                    <div class="setting-label-container">
                        <span class="setting-title">Anisotropy Ratio</span>
                        <span id="anisotropy-val" class="setting-val">15.0</span>
                    </div>
                    <input type="range" id="anisotropy-slider" class="setting-slider" min="1.0" max="50.0" step="1.0" value="15.0" oninput="onAnisotropyChange(this.value)">
                </div>

                <div class="setting-row">
                    <div class="setting-label-container">
                        <span class="setting-title">Low-Pass Blur</span>
                        <span id="lowpass-val" class="setting-val">0.10 px</span>
                    </div>
                    <input type="range" id="lowpass-slider" class="setting-slider" min="0.0" max="0.5" step="0.01" value="0.10" oninput="onLowpassChange(this.value)">
                </div>

                <div class="preset-container">
                    <div id="preset-balanced" class="preset-btn active" onclick="applyPreset('balanced')">Balanced</div>
                    <div id="preset-supersplat" class="preset-btn" onclick="applyPreset('supersplat')">SuperSplat</div>
                    <div id="preset-smooth" class="preset-btn" onclick="applyPreset('smooth')">Soft/Dense</div>
                </div>
            </div>
            <div id="canvas-container"></div>

            <script>
                console.log("[JS-HTML-LOAD] HTML template initialized. Build Timestamp: ${System.currentTimeMillis()}");
                
                // Initialize global parameters on window so they persist across model loads
                window.splatScaleVal = window.splatScaleVal || 1.0;
                window.lowPassVal = window.lowPassVal || 0.10;
                window.maxAnisotropyVal = window.maxAnisotropyVal || 15.0;

                let scene, camera, renderer, controls, pointsMesh;
                let isDemoScene = true;

                // Gaussian Splat state variables
                let activeSplatGeometry = null;
                let activeSplatVertexCount = 0;
                let splatIndices = null;
                let splatDepths = null;
                let indexList = [];
                let lastCameraPosition = new THREE.Vector3();
                let frameCount = 0;

                // Base attributes from PLY parser
                let basePositions = null;
                let baseColors = null;
                let baseOpacities = null;
                let baseScales = null;
                let baseRotations = null;
                let sortedDataArray = null;
                let interleavedBuffer = null;

                init();
                animate();

                function init() {
                    const container = document.getElementById('canvas-container');

                    // 1. Scene setup
                    scene = new THREE.Scene();
                    scene.fog = new THREE.FogExp2(0x1C1B1F, 0.05);

                    // 2. Camera setup
                    camera = new THREE.PerspectiveCamera(60, window.innerWidth / window.innerHeight, 0.1, 100);
                    camera.position.set(0, 2, 4);

                    // 3. Renderer
                    renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
                    renderer.setPixelRatio(window.devicePixelRatio);
                    renderer.setSize(window.innerWidth, window.innerHeight);
                    container.appendChild(renderer.domElement);

                    // 4. Controls
                    controls = new THREE.OrbitControls(camera, renderer.domElement);
                    controls.enableDamping = true;
                    controls.dampingFactor = 0.05;
                    controls.screenSpacePanning = true;
                    controls.minDistance = 0.5;
                    controls.maxDistance = 15;
                    
                    // CRITICAL FIX: Set polar angle limits slightly away from 0 and Math.PI 
                    // to prevent gimbal lock/polar singularity freezing near the poles!
                    controls.minPolarAngle = 0.0005;
                    controls.maxPolarAngle = Math.PI - 0.0005;
                    
                    controls.touches = {
                        ONE: THREE.TOUCH.ROTATE,
                        TWO: THREE.TOUCH.DOLLY_PAN
                    };

                    // 5. Lights
                    const ambientLight = new THREE.AmbientLight(0xffffff, 0.8);
                    scene.add(ambientLight);

                    const dirLight = new THREE.DirectionalLight(0xffffff, 1.0);
                    dirLight.position.set(5, 10, 7);
                    scene.add(dirLight);

                    // 6. 3D Splat Representation (Generating a beautiful volumetric Gaussian-like point cloud)
                    const count = 18000;
                    const geometry = new THREE.BufferGeometry();
                    const positions = new Float32Array(count * 3);
                    const colors = new Float32Array(count * 3);
                    const opacities = new Float32Array(count);
                    const scales = new Float32Array(count * 3);
                    const rotations = new Float32Array(count * 4);

                    // Make a spherical/torus-like structure representing a scanned 3D Object
                    const color1 = new THREE.Color(0x6750A4); // Brand Purple
                    const color2 = new THREE.Color(0xFFDBCB); // Accent Coral
                    const color3 = new THREE.Color(0xE8DEF8); // Lavender

                    for (let i = 0; i < count; i++) {
                        // Double-torus or organic shape
                        const u = Math.random() * Math.PI * 2;
                        const v = Math.random() * Math.PI * 2;
                        
                        // Parametric formula for Dyson Fan / Vase Shape
                        let r = 0.8 + 0.3 * Math.sin(u * 3) * Math.cos(v);
                        let x = r * Math.sin(u) * Math.cos(v);
                        let y = (Math.random() - 0.5) * 1.8 + 0.2 * Math.sin(u * 5);
                        let z = r * Math.sin(u) * Math.sin(v);

                        positions[i * 3] = x;
                        positions[i * 3 + 1] = y;
                        positions[i * 3 + 2] = z;

                        // Give them Gaussian gradient colors based on coordinates
                        const mixFactor = (y + 1) / 2;
                        const finalColor = new THREE.Color();
                        if (Math.random() > 0.4) {
                            finalColor.lerpColors(color1, color2, mixFactor);
                        } else {
                            finalColor.lerpColors(color2, color3, Math.random());
                        }

                        colors[i * 3] = finalColor.r;
                        colors[i * 3 + 1] = finalColor.g;
                        colors[i * 3 + 2] = finalColor.b;

                        // Gaussian size distribution and splat attributes
                        opacities[i] = 0.85;
                        scales[i * 3] = 0.04 + Math.random() * 0.04;
                        scales[i * 3 + 1] = 0.02 + Math.random() * 0.02;
                        scales[i * 3 + 2] = 0.02 + Math.random() * 0.02;
                        
                        const angle = Math.random() * Math.PI * 2;
                        const rx = Math.sin(angle);
                        const ry = Math.cos(angle);
                        const rz = 0.0;
                        const rw = 1.0;
                        const len = Math.sqrt(rx*rx + ry*ry + rz*rz + rw*rw);
                        rotations[i * 4] = rw / len;
                        rotations[i * 4 + 1] = rx / len;
                        rotations[i * 4 + 2] = ry / len;
                        rotations[i * 4 + 3] = rz / len;
                    }

                    geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
                    geometry.setAttribute('splatColor', new THREE.BufferAttribute(colors, 3));
                    geometry.setAttribute('opacityAttr', new THREE.BufferAttribute(opacities, 1));
                    geometry.setAttribute('scaleAttr', new THREE.BufferAttribute(scales, 3));
                    geometry.setAttribute('rotationAttr', new THREE.BufferAttribute(rotations, 4));

                    renderGeometry(geometry, count);

                    // Add a tiny, elegant wireframe grid base
                    const gridHelper = new THREE.GridHelper(4, 20, 0x6750A4, 0x332F37);
                    gridHelper.position.y = -1.0;
                    gridHelper.material.opacity = 0.25;
                    gridHelper.material.transparent = true;
                    scene.add(gridHelper);

                    window.addEventListener('resize', onWindowResize, false);
                    syncUIFromGlobals();
                }

                function onWindowResize() {
                    camera.aspect = window.innerWidth / window.innerHeight;
                    camera.updateProjectionMatrix();
                    renderer.setSize(window.innerWidth, window.innerHeight);
                    if (pointsMesh && pointsMesh.material && pointsMesh.material.uniforms && pointsMesh.material.uniforms.viewport) {
                        pointsMesh.material.uniforms.viewport.value.set(window.innerWidth, window.innerHeight);
                    }
                }

                function toggleSettingsPanel() {
                    const panel = document.getElementById('settings-panel');
                    const btn = document.getElementById('settings-toggle-btn');
                    if (panel.style.display === 'flex') {
                        panel.style.display = 'none';
                        btn.style.background = 'rgba(103, 80, 164, 0.85)';
                    } else {
                        panel.style.display = 'flex';
                        btn.style.background = 'rgb(103, 80, 164)';
                    }
                }

                function toggleDiagnostics() {
                    const content = document.getElementById('diagnostics-content');
                    const arrow = document.getElementById('diagnostics-arrow');
                    if (content.style.display === 'block') {
                        content.style.display = 'none';
                        arrow.innerHTML = '▸';
                    } else {
                        content.style.display = 'block';
                        arrow.innerHTML = '▾';
                    }
                }

                function updateSplatShaderUniforms() {
                    if (pointsMesh && pointsMesh.material && pointsMesh.material.uniforms) {
                        if (pointsMesh.material.uniforms.splatScale) pointsMesh.material.uniforms.splatScale.value = window.splatScaleVal;
                        if (pointsMesh.material.uniforms.lowPass) pointsMesh.material.uniforms.lowPass.value = window.lowPassVal;
                        if (pointsMesh.material.uniforms.maxAnisotropy) pointsMesh.material.uniforms.maxAnisotropy.value = window.maxAnisotropyVal;
                    }
                }

                function onScaleChange(val) {
                    window.splatScaleVal = parseFloat(val);
                    document.getElementById('scale-val').textContent = window.splatScaleVal.toFixed(2) + 'x';
                    document.getElementById('scale-slider').value = val;
                    updateSplatShaderUniforms();
                    clearPresetActive();
                }

                function onAnisotropyChange(val) {
                    window.maxAnisotropyVal = parseFloat(val);
                    document.getElementById('anisotropy-val').textContent = window.maxAnisotropyVal.toFixed(1);
                    document.getElementById('anisotropy-slider').value = val;
                    updateSplatShaderUniforms();
                    clearPresetActive();
                }

                function onLowpassChange(val) {
                    window.lowPassVal = parseFloat(val);
                    document.getElementById('lowpass-val').textContent = window.lowPassVal.toFixed(2) + ' px';
                    document.getElementById('lowpass-slider').value = val;
                    updateSplatShaderUniforms();
                    clearPresetActive();
                }

                function clearPresetActive() {
                    document.getElementById('preset-balanced').classList.remove('active');
                    document.getElementById('preset-supersplat').classList.remove('active');
                    document.getElementById('preset-smooth').classList.remove('active');
                }

                function applyPreset(presetName) {
                    clearPresetActive();
                    if (presetName === 'balanced') {
                        document.getElementById('preset-balanced').classList.add('active');
                        window.splatScaleVal = 1.0;
                        window.lowPassVal = 0.10;
                        window.maxAnisotropyVal = 15.0;
                    } else if (presetName === 'supersplat') {
                        document.getElementById('preset-supersplat').classList.add('active');
                        window.splatScaleVal = 0.85;
                        window.lowPassVal = 0.04;
                        window.maxAnisotropyVal = 40.0;
                    } else if (presetName === 'smooth') {
                        document.getElementById('preset-smooth').classList.add('active');
                        window.splatScaleVal = 1.40;
                        window.lowPassVal = 0.25;
                        window.maxAnisotropyVal = 3.0;
                    }
                    
                    // Sync UI sliders
                    document.getElementById('scale-slider').value = window.splatScaleVal;
                    document.getElementById('scale-val').textContent = window.splatScaleVal.toFixed(2) + 'x';
                    document.getElementById('anisotropy-slider').value = window.maxAnisotropyVal;
                    document.getElementById('anisotropy-val').textContent = window.maxAnisotropyVal.toFixed(1);
                    document.getElementById('lowpass-slider').value = window.lowPassVal;
                    document.getElementById('lowpass-val').textContent = window.lowPassVal.toFixed(2) + ' px';
                    
                    updateSplatShaderUniforms();
                }

                function resetToDefaults() {
                    applyPreset('balanced');
                }

                function syncUIFromGlobals() {
                    if (document.getElementById('scale-slider')) {
                        document.getElementById('scale-slider').value = window.splatScaleVal;
                        document.getElementById('scale-val').textContent = window.splatScaleVal.toFixed(2) + 'x';
                        document.getElementById('anisotropy-slider').value = window.maxAnisotropyVal;
                        document.getElementById('anisotropy-val').textContent = window.maxAnisotropyVal.toFixed(1);
                        document.getElementById('lowpass-slider').value = window.lowPassVal;
                        document.getElementById('lowpass-val').textContent = window.lowPassVal.toFixed(2) + ' px';
                        
                        clearPresetActive();
                        if (window.splatScaleVal === 1.0 && window.lowPassVal === 0.10 && window.maxAnisotropyVal === 15.0) {
                            document.getElementById('preset-balanced').classList.add('active');
                        } else if (window.splatScaleVal === 0.85 && window.lowPassVal === 0.04 && window.maxAnisotropyVal === 40.0) {
                            document.getElementById('preset-supersplat').classList.add('active');
                        } else if (window.splatScaleVal === 1.40 && window.lowPassVal === 0.25 && window.maxAnisotropyVal === 3.0) {
                            document.getElementById('preset-smooth').classList.add('active');
                        }
                    }
                }

                function animate() {
                    requestAnimationFrame(animate);
                    frameCount++;
                    if (frameCount % 100 === 0) {
                        const camPos = camera.position;
                        const camTarget = controls ? controls.target : new THREE.Vector3();
                        console.log("[JS-RENDER-DEBUG] Frame " + frameCount + " - Render Loop Active.\n" +
                            "  - Camera Position: (" + camPos.x.toFixed(4) + ", " + camPos.y.toFixed(4) + ", " + camPos.z.toFixed(4) + ")\n" +
                            "  - Camera Target: (" + camTarget.x.toFixed(4) + ", " + camTarget.y.toFixed(4) + ", " + camTarget.z.toFixed(4) + ")\n" +
                            "  - Camera Near/Far: " + camera.near + " / " + camera.far
                        );
                    }
                    // Slow rotation to show off 3D form (only for demo scene, not loaded PLYs)
                    if (pointsMesh && isDemoScene) {
                        pointsMesh.rotation.y += 0.003;
                    }
                    
                    if (activeSplatGeometry && pointsMesh) {
                        const localCam = new THREE.Vector3().copy(camera.position);
                        pointsMesh.worldToLocal(localCam);
                        
                        // Only sort if camera has moved significantly to keep performance smooth
                        if (localCam.distanceToSquared(lastCameraPosition) > 0.0001) {
                            lastCameraPosition.copy(localCam);
                            sortSplatsLocal(localCam);
                        }
                    }
                    
                    controls.update();
                    
                    // Update camera diagnostics real-time HUD
                    if (controls) {
                        const polar = controls.getPolarAngle();
                        const azimuth = controls.getAzimuthalAngle();
                        const dist = camera.position.distanceTo(controls.target);
                        const diagElem = document.getElementById('cam-diagnostics');
                        if (diagElem) {
                            const atPole = (polar <= 0.02 || polar >= Math.PI - 0.02);
                            diagElem.innerHTML = 
                                'Polar: ' + polar.toFixed(2) + ' rad' + (atPole ? ' <span style="color:#EF4444;font-weight:bold;">(POLE LIMIT)</span>' : '') + '<br>' +
                                'Azimuth: ' + azimuth.toFixed(2) + ' rad<br>' +
                                'Distance: ' + dist.toFixed(2) + 'm<br>' +
                                'Target: (' + controls.target.x.toFixed(1) + ', ' + controls.target.y.toFixed(1) + ', ' + controls.target.z.toFixed(1) + ')';
                        }
                    }
                    
                    renderer.render(scene, camera);
                }

                // Android bridge interface functions
                function zoomIn() {
                    camera.position.multiplyScalar(0.8);
                }
                function zoomOut() {
                    camera.position.multiplyScalar(1.2);
                }
                function resetView() {
                    camera.position.set(0, 2, 4);
                    controls.target.set(0, 0, 0);
                }
                function rotateX() {
                    if (pointsMesh) pointsMesh.rotation.x += 0.5;
                }
                function rotateY() {
                    if (pointsMesh) pointsMesh.rotation.y += 0.5;
                }

                // Load splat from ArrayBuffer using custom fast binary parser or standard PLYLoader
                function loadSplatFromArrayBuffer(arrayBuffer) {
                    console.log("[JS-PLY-VERSION] loadSplatFromArrayBuffer executing. Timestamp: ${System.currentTimeMillis()}");
                    try {
                        console.log("[JS-PLY] loadSplatFromArrayBuffer initiated. ArrayBuffer byteLength: " + arrayBuffer.byteLength);
                        if (!arrayBuffer || arrayBuffer.byteLength === 0) {
                            throw new Error("Empty or null ArrayBuffer received in loadSplatFromArrayBuffer!");
                        }
                        
                        // Detect and parse binary little endian PLY header to handle custom attributes like Gaussian Splatting Spherical Harmonics (f_dc_*)
                        const headerBytes = new Uint8Array(arrayBuffer);
                        let headerEndOffset = -1;
                        for (let i = 0; i < Math.min(headerBytes.length - 10, 65536); i++) {
                            if (headerBytes[i] === 101 && // 'e'
                                headerBytes[i+1] === 110 && // 'n'
                                headerBytes[i+2] === 100 && // 'd'
                                headerBytes[i+3] === 95 && // '_'
                                headerBytes[i+4] === 104 && // 'h'
                                headerBytes[i+5] === 101 && // 'e'
                                headerBytes[i+6] === 97 && // 'a'
                                headerBytes[i+7] === 100 && // 'd'
                                headerBytes[i+8] === 101 && // 'e'
                                headerBytes[i+9] === 114) { // 'r'
                                headerEndOffset = i + 10;
                                // find next newline character
                                while (headerEndOffset < headerBytes.length && headerBytes[headerEndOffset] !== 10 && headerBytes[headerEndOffset] !== 13) {
                                    headerEndOffset++;
                                }
                                while (headerEndOffset < headerBytes.length && (headerBytes[headerEndOffset] === 10 || headerBytes[headerEndOffset] === 13)) {
                                    headerEndOffset++;
                                }
                                break;
                            }
                        }

                        console.log("[JS-PLY] Header detection completed. headerEndOffset = " + headerEndOffset);

                        if (headerEndOffset !== -1) {
                            const headerStr = new TextDecoder().decode(headerBytes.subarray(0, headerEndOffset));
                            console.log("[JS-PLY] PLY header content:\n" + headerStr);
                            
                            const isBinary = headerStr.includes("binary_little_endian");
                            const vertexMatch = headerStr.match(/element\s+vertex\s+(\d+)/);
                            const vertexCount = vertexMatch ? parseInt(vertexMatch[1]) : 0;
                            console.log("[JS-PLY] isBinary = " + isBinary + ", vertexCount = " + vertexCount);
                            
                            if (isBinary && vertexCount > 0) {
                                console.log("[JS-PLY] Entering optimized binary little endian PLY parser...");
                                const properties = [];
                                const lines = headerStr.split(/\r?\n/);
                                let insideVertexElement = false;
                                for (let line of lines) {
                                    line = line.trim();
                                    if (line.startsWith('element vertex')) {
                                        insideVertexElement = true;
                                        continue;
                                    } else if (line.startsWith('element')) {
                                        insideVertexElement = false;
                                        continue;
                                    }
                                    if (insideVertexElement && line.startsWith('property')) {
                                        const parts = line.split(/\s+/);
                                        const type = parts[1];
                                        const name = parts[2];
                                        properties.push({ type, name });
                                    }
                                }
                                console.log("[JS-PLY] Parsed vertex properties count: " + properties.length);
                                
                                const typeSizes = {
                                    'char': 1, 'uchar': 1, 'short': 2, 'ushort': 2, 'int': 4, 'uint': 4,
                                    'float': 4, 'double': 8, 'float32': 4, 'float64': 8, 'uint8': 1, 'int8': 1,
                                    'uint16': 2, 'int16': 2, 'uint32': 4, 'int32': 4
                                };
                                
                                let stride = 0;
                                const propOffsets = {};
                                for (const prop of properties) {
                                    const size = typeSizes[prop.type] || 4;
                                    propOffsets[prop.name] = {
                                        type: prop.type,
                                        offset: stride,
                                        size: size
                                    };
                                    stride += size;
                                }
                                
                                console.log("[JS-PLY] Computed property offsets: " + JSON.stringify(propOffsets) + " | Total Stride: " + stride);
                                
                                const hasX = 'x' in propOffsets;
                                const hasY = 'y' in propOffsets;
                                const hasZ = 'z' in propOffsets;
                                
                                // Colors
                                const hasSH = 'f_dc_0' in propOffsets && 'f_dc_1' in propOffsets && 'f_dc_2' in propOffsets;
                                const hasRGB = 'red' in propOffsets && 'green' in propOffsets && 'blue' in propOffsets;
                                const hasRGBShort = 'r' in propOffsets && 'g' in propOffsets && 'b' in propOffsets;
                                const hasDiffuse = 'diffuse_red' in propOffsets && 'diffuse_green' in propOffsets && 'diffuse_blue' in propOffsets;
                                
                                console.log("[JS-PLY] Field checks: hasX=" + hasX + ", hasY=" + hasY + ", hasZ=" + hasZ + ", stride=" + stride + " bytes per vertex.");
                                console.log("[JS-PLY] Color modes: hasSH=" + hasSH + ", hasRGB=" + hasRGB + ", hasRGBShort=" + hasRGBShort + ", hasDiffuse=" + hasDiffuse);
                                
                                if (hasX && hasY && hasZ) {
                                    const expectedDataLength = vertexCount * stride;
                                    const actualDataLength = arrayBuffer.byteLength - headerEndOffset;
                                    console.log("[JS-PLY] Data sizing. Expected data size: " + expectedDataLength + " bytes. Actual available data: " + actualDataLength + " bytes.");
                                    
                                    if (actualDataLength < expectedDataLength) {
                                        console.warn("[JS-PLY] WARNING: File appears truncated or stride size mismatch. Proceeding with caution.");
                                    }
                                    
                                    const positions = new Float32Array(vertexCount * 3);
                                    const colors = new Float32Array(vertexCount * 3);
                                    const opacities = new Float32Array(vertexCount);
                                    const scales = new Float32Array(vertexCount * 3);
                                    const rotations = new Float32Array(vertexCount * 4);
                                    const view = new DataView(arrayBuffer, headerEndOffset);
                                    
                                    const SH_C0 = 0.28209479177387814;
                                    
                                    function getPropertyValue(v, base, info) {
                                        const offset = base + info.offset;
                                        if (offset + info.size > actualDataLength) {
                                            // Fallback default value if we overrun due to truncation
                                            return 0;
                                        }
                                        switch (info.type) {
                                            case 'char': case 'int8': return v.getInt8(offset);
                                            case 'uchar': case 'uint8': return v.getUint8(offset);
                                            case 'short': case 'int16': return v.getInt16(offset, true);
                                            case 'ushort': case 'uint16': return v.getUint16(offset, true);
                                            case 'int': case 'int32': return v.getInt32(offset, true);
                                            case 'uint': case 'uint32': return v.getUint32(offset, true);
                                            case 'float': case 'float32': return v.getFloat32(offset, true);
                                            case 'double': case 'float64': return v.getFloat64(offset, true);
                                            default: return v.getFloat32(offset, true);
                                        }
                                    }

                                    const hasOpacity = 'opacity' in propOffsets;
                                    const hasScales = 'scale_0' in propOffsets && 'scale_1' in propOffsets && 'scale_2' in propOffsets;
                                    const hasRotations = 'rot_0' in propOffsets && 'rot_1' in propOffsets && 'rot_2' in propOffsets && 'rot_3' in propOffsets;

                                    let minRawOpacity = Infinity, maxRawOpacity = -Infinity;
                                    let minRawScale = Infinity, maxRawScale = -Infinity;
                                    
                                    for (let i = 0; i < vertexCount; i++) {
                                        const vertexBaseOffset = i * stride;
                                        
                                        // Positions
                                        positions[i * 3] = getPropertyValue(view, vertexBaseOffset, propOffsets['x']);
                                        positions[i * 3 + 1] = getPropertyValue(view, vertexBaseOffset, propOffsets['y']);
                                        positions[i * 3 + 2] = getPropertyValue(view, vertexBaseOffset, propOffsets['z']);
                                        
                                        // Colors
                                        let cr = 0.8, cg = 0.8, cb = 0.8;
                                        if (hasSH) {
                                            const f0 = getPropertyValue(view, vertexBaseOffset, propOffsets['f_dc_0']);
                                            const f1 = getPropertyValue(view, vertexBaseOffset, propOffsets['f_dc_1']);
                                            const f2 = getPropertyValue(view, vertexBaseOffset, propOffsets['f_dc_2']);
                                            cr = Math.min(1.0, Math.max(0.0, f0 * SH_C0 + 0.5));
                                            cg = Math.min(1.0, Math.max(0.0, f1 * SH_C0 + 0.5));
                                            cb = Math.min(1.0, Math.max(0.0, f2 * SH_C0 + 0.5));
                                        } else if (hasRGB) {
                                            const rInfo = propOffsets['red'];
                                            const gInfo = propOffsets['green'];
                                            const bInfo = propOffsets['blue'];
                                            const rVal = getPropertyValue(view, vertexBaseOffset, rInfo);
                                            const gVal = getPropertyValue(view, vertexBaseOffset, gInfo);
                                            const bVal = getPropertyValue(view, vertexBaseOffset, bInfo);
                                            if (rInfo.type === 'uchar' || rInfo.type === 'uint8') {
                                                cr = rVal / 255; cg = gVal / 255; cb = bVal / 255;
                                            } else {
                                                cr = rVal; cg = gVal; cb = bVal;
                                            }
                                        } else if (hasRGBShort) {
                                            const rInfo = propOffsets['r'];
                                            const gInfo = propOffsets['g'];
                                            const bInfo = propOffsets['b'];
                                            const rVal = getPropertyValue(view, vertexBaseOffset, rInfo);
                                            const gVal = getPropertyValue(view, vertexBaseOffset, gInfo);
                                            const bVal = getPropertyValue(view, vertexBaseOffset, bInfo);
                                            if (rInfo.type === 'uchar' || rInfo.type === 'uint8') {
                                                cr = rVal / 255; cg = gVal / 255; cb = bVal / 255;
                                            } else {
                                                cr = rVal; cg = gVal; cb = bVal;
                                            }
                                        } else if (hasDiffuse) {
                                            const rInfo = propOffsets['diffuse_red'];
                                            const gInfo = propOffsets['diffuse_green'];
                                            const bInfo = propOffsets['diffuse_blue'];
                                            const rVal = getPropertyValue(view, vertexBaseOffset, rInfo);
                                            const gVal = getPropertyValue(view, vertexBaseOffset, gInfo);
                                            const bVal = getPropertyValue(view, vertexBaseOffset, bInfo);
                                            if (rInfo.type === 'uchar' || rInfo.type === 'uint8') {
                                                cr = rVal / 255; cg = gVal / 255; cb = bVal / 255;
                                            } else {
                                                cr = rVal; cg = gVal; cb = bVal;
                                            }
                                        }
                                        
                                        colors[i * 3] = cr;
                                        colors[i * 3 + 1] = cg;
                                        colors[i * 3 + 2] = cb;

                                        // Opacity: GS logit to sigmoid
                                        let rawO = 0.0;
                                        if (hasOpacity) {
                                            rawO = getPropertyValue(view, vertexBaseOffset, propOffsets['opacity']);
                                            if (rawO < minRawOpacity) minRawOpacity = rawO;
                                            if (rawO > maxRawOpacity) maxRawOpacity = rawO;
                                        }
                                        const op = hasOpacity ? (1.0 / (1.0 + Math.exp(-rawO))) : 1.0;
                                        opacities[i] = op;

                                        // Scale: GS log-scale to exp
                                        let s0 = 0.0, s1 = 0.0, s2 = 0.0;
                                        if (hasScales) {
                                            s0 = getPropertyValue(view, vertexBaseOffset, propOffsets['scale_0']);
                                            s1 = getPropertyValue(view, vertexBaseOffset, propOffsets['scale_1']);
                                            s2 = getPropertyValue(view, vertexBaseOffset, propOffsets['scale_2']);
                                            if (s0 < minRawScale) minRawScale = s0;
                                            if (s0 > maxRawScale) maxRawScale = s0;
                                            
                                            scales[i * 3] = Math.exp(s0);
                                            scales[i * 3 + 1] = Math.exp(s1);
                                            scales[i * 3 + 2] = Math.exp(s2);
                                        } else {
                                            scales[i * 3] = 0.05;
                                            scales[i * 3 + 1] = 0.05;
                                            scales[i * 3 + 2] = 0.05;
                                        }

                                        // Rotation: unit quaternion (w, x, y, z)
                                        let r0 = 1.0, r1 = 0.0, r2 = 0.0, r3 = 0.0;
                                        if (hasRotations) {
                                            r0 = getPropertyValue(view, vertexBaseOffset, propOffsets['rot_0']);
                                            r1 = getPropertyValue(view, vertexBaseOffset, propOffsets['rot_1']);
                                            r2 = getPropertyValue(view, vertexBaseOffset, propOffsets['rot_2']);
                                            r3 = getPropertyValue(view, vertexBaseOffset, propOffsets['rot_3']);
                                            const len = Math.sqrt(r0*r0 + r1*r1 + r2*r2 + r3*r3);
                                            if (len > 0.0) {
                                                rotations[i * 4] = r0 / len;
                                                rotations[i * 4 + 1] = r1 / len;
                                                rotations[i * 4 + 2] = r2 / len;
                                                rotations[i * 4 + 3] = r3 / len;
                                            } else {
                                                rotations[i * 4] = 1.0;
                                                rotations[i * 4 + 1] = 0.0;
                                                rotations[i * 4 + 2] = 0.0;
                                                rotations[i * 4 + 3] = 0.0;
                                            }
                                        } else {
                                            rotations[i * 4] = 1.0;
                                            rotations[i * 4 + 1] = 0.0;
                                            rotations[i * 4 + 2] = 0.0;
                                            rotations[i * 4 + 3] = 0.0;
                                        }

                                        // Console log the raw parsed values for the first 5 splats as requested
                                        if (i < 10) {
                                            console.log("[JS-PLY-DEBUG] Splat " + i + " Info:\n" +
                                                "  - RAW PLY VALUES:\n" +
                                                "    pos=(" + positions[i*3].toFixed(6) + ", " + positions[i*3+1].toFixed(6) + ", " + positions[i*3+2].toFixed(6) + ")\n" +
                                                "    scale_raw=(" + s0.toFixed(6) + ", " + s1.toFixed(6) + ", " + s2.toFixed(6) + ")\n" +
                                                "    rot_raw=(" + r0.toFixed(6) + ", " + r1.toFixed(6) + ", " + r2.toFixed(6) + ", " + r3.toFixed(6) + ")\n" +
                                                "    opacity_raw=" + rawO.toFixed(6) + "\n" +
                                                "  - PROCESSED IN-MEMORY VALUES:\n" +
                                                "    scale_exp=(" + scales[i*3].toFixed(6) + ", " + scales[i*3+1].toFixed(6) + ", " + scales[i*3+2].toFixed(6) + ")\n" +
                                                "    rot_norm=(" + rotations[i*4].toFixed(6) + ", " + rotations[i*4+1].toFixed(6) + ", " + rotations[i*4+2].toFixed(6) + ", " + rotations[i*4+3].toFixed(6) + ")\n" +
                                                "    opacity_sig=" + opacities[i].toFixed(6)
                                            );
                                        }
                                    }

                                    let minScaleParsed = Infinity;
                                    let maxScaleParsed = -Infinity;
                                    if (hasScales && vertexCount > 0) {
                                        for (let i = 0; i < scales.length; i++) {
                                            const val = scales[i];
                                            if (val < minScaleParsed) minScaleParsed = val;
                                            if (val > maxScaleParsed) maxScaleParsed = val;
                                        }
                                    }

                                    console.log("[JS-PLY] Parsed stats: vertexCount=" + vertexCount + 
                                                ", minRawOpacity=" + (minRawOpacity === Infinity ? "N/A" : minRawOpacity.toFixed(4)) + 
                                                ", maxRawOpacity=" + (maxRawOpacity === -Infinity ? "N/A" : maxRawOpacity.toFixed(4)) + 
                                                ", minRawScale_0=" + (minRawScale === Infinity ? "N/A" : minRawScale.toFixed(4)) + 
                                                ", maxRawScale_0=" + (maxRawScale === -Infinity ? "N/A" : maxRawScale.toFixed(4)));
                                    
                                    console.log("[JS-PLY] Scale statistics: " +
                                                "minScale=" + minScaleParsed.toFixed(6) + 
                                                ", maxScale=" + maxScaleParsed.toFixed(6));

                                    // === STEP 1: Percentile diagnostics ===
                                    let p50 = 0, p95 = 0, p99 = 0;
                                    if (hasScales && vertexCount > 0) {
                                        const magnitudes = new Float32Array(vertexCount);
                                        for (let i = 0; i < vertexCount; i++) {
                                            magnitudes[i] = Math.max(scales[i*3], scales[i*3+1], scales[i*3+2]);
                                        }
                                        const sorted = Array.from(magnitudes).sort((a,b) => a - b);
                                        p50 = sorted[Math.floor(vertexCount * 0.50)];
                                        p95 = sorted[Math.floor(vertexCount * 0.95)];
                                        p99 = sorted[Math.floor(vertexCount * 0.99)];
                                        const maxMag = sorted[vertexCount - 1];
                                        console.log("[JS-PLY] Scale magnitude percentiles: p50=" + p50.toFixed(5) +
                                                    ", p95=" + p95.toFixed(5) + ", p99=" + p99.toFixed(5) + ", max=" + maxMag.toFixed(5));
                                    }

                                    // === STEP 2: Outlier/floater culling ===
                                    // === STEP 2: Outlier/floater culling ===
                                    let culledCount = 0;
                                    if (hasScales && vertexCount > 0 && p99 > 0) {
                                        const threshold = p99 * 2.0; // tune this multiplier after seeing your real percentiles
                                        for (let i = 0; i < vertexCount; i++) {
                                            const mag = Math.max(scales[i*3], scales[i*3+1], scales[i*3+2]);
                                            if (mag > threshold) {
                                                opacities[i] = 0.0; // hide instead of deleting, keeps buffer indices intact
                                                culledCount++;
                                            }
                                        }
                                        console.log("[JS-PLY] Culled " + culledCount + " outlier/floater splats above threshold " + threshold.toFixed(5));
                                    }

                                    // On-screen diagnostic readout (no console/adb needed)
                                    document.getElementById('stats').innerHTML =
                                        'Vertices: ' + vertexCount.toLocaleString() + '<br>' +
                                        'p50=' + p50.toFixed(4) + ' p95=' + p95.toFixed(4) + ' p99=' + p99.toFixed(4) + '<br>' +
                                        'Culled: ' + culledCount;
                                    
                                    console.log("[JS-PLY] Optimized parsing successful. Creating BufferGeometry...");
                                    const geometry = new THREE.BufferGeometry();
                                    geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
                                    geometry.setAttribute('splatColor', new THREE.BufferAttribute(colors, 3));
                                    geometry.setAttribute('opacityAttr', new THREE.BufferAttribute(opacities, 1));
                                    geometry.setAttribute('scaleAttr', new THREE.BufferAttribute(scales, 3));
                                    geometry.setAttribute('rotationAttr', new THREE.BufferAttribute(rotations, 4));
                                    geometry.userData = { isPointCloud: !hasRotations || !hasScales };
                                    
                                    renderGeometry(geometry, vertexCount);
                                    isDemoScene = false;
                                    return;
                                } else {
                                    console.warn("[JS-PLY] Skipping optimized binary parser: x, y, or z properties are missing in propOffsets!");
                                }
                            } else {
                                console.log("[JS-PLY] PLY file is not a binary format or vertexCount is 0.");
                            }
                        } else {
                            console.warn("[JS-PLY] WARNING: 'end_header' marker was not found in the first 64KB!");
                        }
                        
                        // Fallback to PLYLoader for standard ASCII or other formats
                        console.log("[JS-PLY] Falling back to standard THREE.PLYLoader");
                        const loader = new THREE.PLYLoader();
                        const geometry = loader.parse(arrayBuffer);
                        
                        if (geometry) {
                            console.log("[JS-PLY] THREE.PLYLoader parse returned geometry successfully.");
                            geometry.userData = { isPointCloud: true };
                            if (geometry.attributes.position) {
                                console.log("[JS-PLY] Found position attribute. Count: " + geometry.attributes.position.count);
                            } else {
                                console.error("[JS-PLY] PLYLoader returned a geometry, but no position attribute exists!");
                            }
                        } else {
                            throw new Error("PLYLoader.parse returned null or undefined geometry.");
                        }
                        
                        if (geometry && !geometry.attributes.color && !geometry.attributes.splatColor && geometry.attributes.position) {
                            console.log("[JS-PLY] No vertex colors found in geometry. Generating default gradient.");
                            const count = geometry.attributes.position.count;
                            const colors = new Float32Array(count * 3);
                            const color1 = new THREE.Color(0x6750A4);
                            const color2 = new THREE.Color(0xFFDBCB);
                            
                            const posAttr = geometry.attributes.position;
                            for (let i = 0; i < count; i++) {
                                const y = posAttr.getY(i);
                                const mixFactor = Math.min(1.0, Math.max(0.0, (y + 1.0) / 2.0));
                                const c = new THREE.Color().lerpColors(color1, color2, mixFactor);
                                colors[i * 3] = c.r;
                                colors[i * 3 + 1] = c.g;
                                colors[i * 3 + 2] = c.b;
                            }
                            geometry.setAttribute('splatColor', new THREE.BufferAttribute(colors, 3));
                        }
                        
                        const calculatedVertices = geometry.attributes.position ? geometry.attributes.position.count : 0;
                        console.log("[JS-PLY] Finalizing fallback rendering. Vertices count: " + calculatedVertices);
                        renderGeometry(geometry, calculatedVertices);
                        isDemoScene = false;
                        
                    } catch (e) {
                        console.error("[JS-PLY] CRITICAL ERROR parsing PLY array buffer:", e);
                        document.getElementById('stats').innerText = 'Error parsing 3D data: ' + e.message;
                    }
                }

                function renderGeometry(geometry, vertexCount) {
                    try {
                        console.log("[JS-PLY] renderGeometry triggered with vertexCount: " + vertexCount);
                        if (pointsMesh) {
                            scene.remove(pointsMesh);
                        }

                        // Map standard color attribute to splatColor if only color exists
                        if (!geometry.attributes.splatColor && geometry.attributes.color) {
                            geometry.setAttribute('splatColor', geometry.attributes.color);
                        }

                        // Generate default attributes if they are missing (e.g. fallback PLYLoader or demo fallback)
                        if (!geometry.attributes.opacityAttr && geometry.attributes.position) {
                            const count = geometry.attributes.position.count;
                            const opacities = new Float32Array(count);
                            const scales = new Float32Array(count * 3);
                            const rotations = new Float32Array(count * 4);
                            
                            for (let i = 0; i < count; i++) {
                                opacities[i] = 0.9;
                                scales[i * 3] = 0.04;
                                scales[i * 3 + 1] = 0.04;
                                scales[i * 3 + 2] = 0.04;
                                rotations[i * 4] = 1.0;
                                rotations[i * 4 + 1] = 0.0;
                                rotations[i * 4 + 2] = 0.0;
                                rotations[i * 4 + 3] = 0.0;
                            }
                            geometry.setAttribute('opacityAttr', new THREE.BufferAttribute(opacities, 1));
                            geometry.setAttribute('scaleAttr', new THREE.BufferAttribute(scales, 3));
                            geometry.setAttribute('rotationAttr', new THREE.BufferAttribute(rotations, 4));
                        }

                        const isPointCloud = !!(geometry.userData && geometry.userData.isPointCloud);
                        console.log("[JS-PLY] renderGeometry isPointCloud: " + isPointCloud);

                        // Define base quad geometry (2 triangles covering the splat)
                        const baseGeometry = new THREE.BufferGeometry();
                        const vertices = new Float32Array([
                            -1.5, -1.5, 0,
                             1.5, -1.5, 0,
                             1.5,  1.5, 0,
                            -1.5,  1.5, 0
                        ]);
                        const indices = new Uint16Array([
                            0, 1, 2,
                            0, 2, 3
                        ]);
                        baseGeometry.setAttribute('position', new THREE.BufferAttribute(vertices, 3));
                        baseGeometry.setIndex(new THREE.BufferAttribute(indices, 1));

                        const instancedGeometry = new THREE.InstancedBufferGeometry().copy(baseGeometry);
                        instancedGeometry.instanceCount = vertexCount;

                        // Allocate and setup the single interleaved buffer for all splat attributes (14 floats per instance)
                        sortedDataArray = new Float32Array(vertexCount * 14);
                        interleavedBuffer = new THREE.InstancedInterleavedBuffer(sortedDataArray, 14);
                        interleavedBuffer.setUsage(THREE.DynamicDrawUsage);

                        instancedGeometry.setAttribute('splatPosition', new THREE.InterleavedBufferAttribute(interleavedBuffer, 3, 0));
                        instancedGeometry.setAttribute('splatColor', new THREE.InterleavedBufferAttribute(interleavedBuffer, 3, 3));
                        instancedGeometry.setAttribute('scaleAttr', new THREE.InterleavedBufferAttribute(interleavedBuffer, 3, 6));
                        instancedGeometry.setAttribute('rotationAttr', new THREE.InterleavedBufferAttribute(interleavedBuffer, 4, 9));
                        instancedGeometry.setAttribute('opacityAttr', new THREE.InterleavedBufferAttribute(interleavedBuffer, 1, 13));

                        // Save base attributes for depth sorting CPU updates
                        basePositions = geometry.attributes.position ? geometry.attributes.position.array : null;
                        baseColors = geometry.attributes.splatColor ? geometry.attributes.splatColor.array : null;
                        baseOpacities = geometry.attributes.opacityAttr ? geometry.attributes.opacityAttr.array : null;
                        baseScales = geometry.attributes.scaleAttr ? geometry.attributes.scaleAttr.array : null;
                        baseRotations = geometry.attributes.rotationAttr ? geometry.attributes.rotationAttr.array : null;

                        activeSplatGeometry = instancedGeometry;
                        activeSplatVertexCount = vertexCount;

                        // Populate initial index list
                        splatIndices = new Uint32Array(vertexCount);
                        indexList = new Array(vertexCount);
                        for (let i = 0; i < vertexCount; i++) {
                            splatIndices[i] = i;
                            indexList[i] = i;
                        }
                        splatDepths = new Float32Array(vertexCount);

                        // Sort once initially
                        const localCam = new THREE.Vector3().copy(camera.position);
                        sortSplatsLocal(localCam);

                        // Shader-based true Gaussian Splat material with Instancing
                        const material = new THREE.ShaderMaterial({
                            uniforms: {
                                viewport: { value: new THREE.Vector2(window.innerWidth, window.innerHeight) },
                                isPointCloud: { value: isPointCloud },
                                splatScale: { value: window.splatScaleVal !== undefined ? window.splatScaleVal : 1.0 },
                                lowPass: { value: window.lowPassVal !== undefined ? window.lowPassVal : 0.10 },
                                maxAnisotropy: { value: window.maxAnisotropyVal !== undefined ? window.maxAnisotropyVal : 15.0 }
                            },
                            vertexShader: `
                                precision highp float;
                                precision highp int;
                                
                                // Instanced splat attributes
                                attribute vec3 splatPosition;
                                attribute vec3 splatColor;
                                attribute vec3 scaleAttr;
                                attribute vec4 rotationAttr;
                                attribute float opacityAttr;
                                
                                varying vec2 v_offset;
                                varying vec3 v_conic;
                                varying vec3 v_color;
                                varying float v_opacity;
                                
                                uniform vec2 viewport;
                                uniform bool isPointCloud;
                                uniform float splatScale;
                                uniform float lowPass;
                                uniform float maxAnisotropy;
                                
                                void main() {
                                    v_color = splatColor;
                                    v_opacity = opacityAttr;
                                    
                                    vec4 camPos = modelViewMatrix * vec4(splatPosition, 1.0);
                                    gl_Position = projectionMatrix * camPos;
                                    
                                    if (isPointCloud) {
                                        float s = clamp(15.0 / -camPos.z, 2.0, 32.0);
                                        v_conic = vec3(1.0 / (s * s), 0.0, 1.0 / (s * s));
                                        vec2 quadOffset = position.xy * s;
                                        v_offset = quadOffset;
                                        gl_Position.xy += quadOffset / viewport * gl_Position.w;
                                        return;
                                    }
                                    
                                    vec3 scale = clamp(scaleAttr, vec3(0.00001), vec3(10.0)) * splatScale;
                                    
                                    // Limit anisotropy based on maxAnisotropy to prevent needle spikes or allow pure ellipses
                                    float maxS = max(scale.x, max(scale.y, scale.z));
                                    if (maxAnisotropy > 1.0) {
                                        scale = max(scale, vec3(maxS / maxAnisotropy));
                                    }
                                    
                                    float r = rotationAttr.x;
                                    float x = rotationAttr.y;
                                    float y = rotationAttr.z;
                                    float z = rotationAttr.w;
                                    
                                    mat3 R = mat3(
                                        vec3(1.0 - 2.0 * (y*y + z*z), 2.0 * (x*y + r*z), 2.0 * (x*z - r*y)), // column 0
                                        vec3(2.0 * (x*y - r*z), 1.0 - 2.0 * (x*x + z*z), 2.0 * (y*z + r*x)), // column 1
                                        vec3(2.0 * (x*z + r*y), 2.0 * (y*z - r*x), 1.0 - 2.0 * (x*x + y*y))  // column 2
                                    );
                                    
                                    mat3 M = mat3(
                                        R[0] * scale.x,
                                        R[1] * scale.y,
                                        R[2] * scale.z
                                    );
                                    
                                    mat3 MT = mat3(
                                        M[0][0], M[1][0], M[2][0],
                                        M[0][1], M[1][1], M[2][1],
                                        M[0][2], M[1][2], M[2][2]
                                    );
                                    mat3 Sigma = M * MT;
                                    
                                    mat3 R_mv = mat3(modelViewMatrix);
                                    mat3 R_mv_T = mat3(
                                        R_mv[0][0], R_mv[1][0], R_mv[2][0],
                                        R_mv[0][1], R_mv[1][1], R_mv[2][1],
                                        R_mv[0][2], R_mv[1][2], R_mv[2][2]
                                    );
                                    mat3 Sigma_cam = R_mv * Sigma * R_mv_T;
                                    
                                    float fx = projectionMatrix[0][0] * viewport.x * 0.5;
                                    float fy = projectionMatrix[1][1] * viewport.y * 0.5;
                                    
                                    float t_z = camPos.z;
                                    float z_val = -t_z;
                                    if (z_val <= 0.01) {
                                        gl_Position = vec4(0.0);
                                        return;
                                    }
                                    
                                    float J00 = fx / z_val;
                                    float J02 = - (fx * camPos.x) / (z_val * z_val);
                                    float J11 = fy / z_val;
                                    float J12 = - (fy * camPos.y) / (z_val * z_val);
                                    
                                    float V00 = J00 * J00 * Sigma_cam[0][0] + 2.0 * J00 * J02 * Sigma_cam[0][2] + J02 * J02 * Sigma_cam[2][2];
                                    float V11 = J11 * J11 * Sigma_cam[1][1] + 2.0 * J11 * J12 * Sigma_cam[1][2] + J12 * J12 * Sigma_cam[2][2];
                                    float V01 = J00 * J11 * Sigma_cam[0][1] + J00 * J12 * Sigma_cam[0][2] + J11 * J02 * Sigma_cam[1][2] + J02 * J12 * Sigma_cam[2][2];
                                    
                                    V00 += lowPass;
                                    V11 += lowPass;
                                    
                                    float trace = V00 + V11;
                                    float det = max(V00 * V11 - V01 * V01, 1e-4);
                                    
                                    float discriminant = max(0.0, trace * trace - 4.0 * det);
                                    float lambda_max = max(0.0001, (trace + sqrt(discriminant)) * 0.5);
                                    float lambda_min = max(0.0001, (trace - sqrt(discriminant)) * 0.5);
                                    
                                    float s1 = 3.0 * sqrt(lambda_max);
                                    float s2 = 3.0 * sqrt(lambda_min);
                                    
                                    // Continuously compute the exact orientation angle theta of the 2D covariance ellipse
                                    float theta = 0.5 * atan(2.0 * V01, V00 - V11);
                                    vec2 axis1 = vec2(cos(theta), sin(theta));
                                    vec2 axis2 = vec2(-axis1.y, axis1.x);
                                    
                                    vec2 quadOffset = position.x * s1 * axis1 + position.y * s2 * axis2;
                                    v_offset = quadOffset;
                                    v_conic = vec3(V11, -V01, V00) / det;
                                    
                                    gl_Position.xy += quadOffset / viewport * gl_Position.w;
                                }
                            `,
                            fragmentShader: `
                                precision highp float;
                                precision highp int;
                                
                                varying vec2 v_offset;
                                varying vec3 v_conic;
                                varying vec3 v_color;
                                varying float v_opacity;
                                
                                void main() {
                                    float power = -0.5 * (v_offset.x * v_offset.x * v_conic.x + 
                                                          2.0 * v_offset.x * v_offset.y * v_conic.y + 
                                                          v_offset.y * v_offset.y * v_conic.z);
                                    
                                    if (power > 0.0) {
                                        discard;
                                    }
                                    if (power < -8.0) {
                                        discard;
                                    }
                                    
                                    float alpha = exp(power) * v_opacity;
                                    gl_FragColor = vec4(v_color, alpha);
                                }
                            `,
                            transparent: true,
                            depthWrite: false,
                            depthTest: true,
                            blending: THREE.NormalBlending,
                            side: THREE.DoubleSide
                        });

                        pointsMesh = new THREE.Mesh(instancedGeometry, material);
                        pointsMesh.frustumCulled = false;
                        console.log("[JS-PLY] instanceCount set to: " + instancedGeometry.instanceCount + ", drawRange: " + JSON.stringify(instancedGeometry.drawRange));
                        scene.add(pointsMesh);

                        // Recenter around bounding sphere
                        geometry.computeBoundingSphere();
                        const sphere = geometry.boundingSphere;
                        if (sphere) {
                            instancedGeometry.boundingSphere = sphere.clone();
                            if (geometry.boundingBox) {
                                instancedGeometry.boundingBox = geometry.boundingBox.clone();
                            }
                            controls.target.copy(sphere.center);
                            camera.position.set(sphere.center.x, sphere.center.y + sphere.radius, sphere.center.z + sphere.radius * 1.5);
                            controls.update();
                            console.log("[JS-PLY] Scene camera re-centered around bounding sphere: center=" + JSON.stringify(sphere.center) + ", radius=" + sphere.radius);
                        } else {
                            console.warn("[JS-PLY] Geometry does not have a valid bounding sphere.");
                        }
                        console.log("[JS-PLY] Successfully rendered geometry to canvas!");
                    } catch (e) {
                        console.error("[JS-PLY] Error in renderGeometry:", e);
                        document.getElementById('stats').innerText = 'Render Error: ' + e.message;
                    }
                }

                function sortSplatsLocal(localCam) {
                    if (!activeSplatGeometry || !basePositions) return;
                    
                    const lcx = localCam.x;
                    const lcy = localCam.y;
                    const lcz = localCam.z;
                    
                    const pos = basePositions;
                    const count = activeSplatVertexCount;
                    
                    for (let i = 0; i < count; i++) {
                        const dx = pos[i * 3] - lcx;
                        const dy = pos[i * 3 + 1] - lcy;
                        const dz = pos[i * 3 + 2] - lcz;
                        splatDepths[i] = dx * dx + dy * dy + dz * dz;
                    }
                    
                    indexList.sort((a, b) => splatDepths[b] - splatDepths[a]);
                    
                    for (let i = 0; i < count; i++) {
                        const origIdx = indexList[i];
                        splatIndices[i] = origIdx;
                        
                        const base = i * 14;
                        
                        // Position
                        sortedDataArray[base] = pos[origIdx * 3];
                        sortedDataArray[base + 1] = pos[origIdx * 3 + 1];
                        sortedDataArray[base + 2] = pos[origIdx * 3 + 2];
                        
                        // Color
                        if (baseColors) {
                            sortedDataArray[base + 3] = baseColors[origIdx * 3];
                            sortedDataArray[base + 4] = baseColors[origIdx * 3 + 1];
                            sortedDataArray[base + 5] = baseColors[origIdx * 3 + 2];
                        } else {
                            sortedDataArray[base + 3] = 1.0;
                            sortedDataArray[base + 4] = 1.0;
                            sortedDataArray[base + 5] = 1.0;
                        }
                        
                        // Scale
                        if (baseScales) {
                            sortedDataArray[base + 6] = baseScales[origIdx * 3];
                            sortedDataArray[base + 7] = baseScales[origIdx * 3 + 1];
                            sortedDataArray[base + 8] = baseScales[origIdx * 3 + 2];
                        } else {
                            sortedDataArray[base + 6] = 0.04;
                            sortedDataArray[base + 7] = 0.04;
                            sortedDataArray[base + 8] = 0.04;
                        }
                        
                        // Rotation
                        if (baseRotations) {
                            sortedDataArray[base + 9] = baseRotations[origIdx * 4];
                            sortedDataArray[base + 10] = baseRotations[origIdx * 4 + 1];
                            sortedDataArray[base + 11] = baseRotations[origIdx * 4 + 2];
                            sortedDataArray[base + 12] = baseRotations[origIdx * 4 + 3];
                        } else {
                            sortedDataArray[base + 9] = 1.0;
                            sortedDataArray[base + 10] = 0.0;
                            sortedDataArray[base + 11] = 0.0;
                            sortedDataArray[base + 12] = 0.0;
                        }
                        
                        // Opacity
                        if (baseOpacities) {
                            sortedDataArray[base + 13] = baseOpacities[origIdx];
                        } else {
                            sortedDataArray[base + 13] = 0.9;
                        }
                    }
                    
                    interleavedBuffer.needsUpdate = true;
                }

                // Global function to fetch and load the PLY from URL
                window.loadSplatFromUrl = function(url) {
                    console.log("[JS-PLY] loadSplatFromUrl initiated. Target URL: " + url);
                    fetch(url)
                        .then(response => {
                            console.log("[JS-PLY] Fetch HTTP response received. Status: " + response.status + " (" + response.statusText + ")");
                            console.log("[JS-PLY] Content-Type: " + response.headers.get("Content-Type") + ", Content-Length: " + response.headers.get("Content-Length"));
                            if (!response.ok) {
                                throw new Error("Fetch failed with HTTP Status " + response.status + " " + response.statusText);
                            }
                            return response.arrayBuffer();
                        })
                        .then(arrayBuffer => {
                            console.log("[JS-PLY] Successfully fetched ArrayBuffer. Length: " + arrayBuffer.byteLength + " bytes");
                            loadSplatFromArrayBuffer(arrayBuffer);
                        })
                        .catch(e => {
                            console.error("[JS-PLY] CRITICAL fetch/load error:", e);
                            document.getElementById('stats').innerText = 'Error loading 3D data: ' + e.message;
                        });
                };

                // Define the GaussianSplats3D structure as requested
                window.GaussianSplats3D = {
                    Viewer: {
                        addSplatScene: function(uint8Array) {
                            console.log("addSplatScene called with Uint8Array of length: " + uint8Array.length);
                            loadSplatFromArrayBuffer(uint8Array.buffer);
                        }
                    }
                };
            </script>
        </body>
        </html>
        """.trimIndent()
    }
}