# Terrarium
**Pixel-Perfect Earth Terrain for Minecraft**  

Generate 1:1 block-scale worlds from real elevation data, with dynamic tile loading and customizable terrain shaping.  

---

## 🌐 Terrain Data Pipeline
- **Source**: AWS S3 (`elevation-tiles-prod/terrarium/{z}/{x}/{y}.png`)  
- **Pixel-to-Block**: 1 pixel = 1 Minecraft block.  
- **Tile System**:  
  - Each tile = `256×256` pixels (`256×256` blocks ingame).  
  - `zoom` level determines tile count per axis (`2^zoom` tiles).  
    - Example: `zoom: 8` = `2⁸ = 256` tiles → `256×256 = 65,536` blocks wide.  

---

## 🗃️ Downloads
- While it is still at Alpha quality, you can download indev test versions [here](https://nightly.link/ly-nxs/terrarium/workflows/build/1.21-World/Artifacts)
- Support is not guarunteed
---

## 🏔️ Technical Notes
- **Scale Examples**:
  - zoom: 10 = 1,024 tiles → 262,144×262,144 blocks.
  - zoom: 13 = 8,192 tiles → 2,097,152×2,097,152 blocks.

### 🌩️ Performance:
- Higher zoom = larger worlds but slower generation.
- Reduce zoom to 8–10 for survival-friendly sizes.
### World Size Comparison
- zoom: 10 (small - ~1:48) vs. zoom: 13 (planetary-scale - ~1:6).
### 🖼️ Screenshots:

**Zoom: 11 | Height 512**

---

![2025-03-25_18 19 55](https://github.com/user-attachments/assets/37395fcc-af8a-4820-a509-c42f8bb4f2ae)

**Zoom: 12 | Height 512**

---

![2025-03-27_16 19 16](https://github.com/user-attachments/assets/bb86d657-94ea-4a5d-8362-e8f2cb41973f)
![2025-03-28_07 52 57](https://github.com/user-attachments/assets/81023cbc-3e84-4095-b39b-7e536f4c494b)

**Zoom: 13 | Height 768**

---

![2025-03-28_15 59 45](https://github.com/user-attachments/assets/840424dd-6c90-4049-b2da-cc862ed5b853)
![2025-03-28_15 37 18](https://github.com/user-attachments/assets/45c44fd1-1eb3-4b3c-b9ca-2439bf6d53be)

---

## 🛠️ How It Works
- Tile Fetching: Downloads 256×256 PNG tiles from AWS based on zoom and caches.
- Height Mapping: Converts RGB pixels to block heights, scaled by additionalAlt.
- Biome Placement: Vanilla biomes mapped using elevation (startingY + altitudeDropoff).

## 🔧 **Configuration**  
`/config/BlossomMods/Terrarium.json`:  
```json
{
  "additionalAlt": 1.0,    
  "zoom": 13,   // 2¹³ tiles = 8,192 tiles wide (≈2M blocks at 256px/tile).
  "worldHeight": 768,       // Max Y height.
  "altitudeDropoff": 0.5,
  "startingY": 0,           // Base height offset (negative for deeper oceans).
  "stoneDepth": 32      
}
