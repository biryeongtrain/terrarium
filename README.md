# Terrarium
**Pixel-Perfect Earth Terrain for Minecraft**  

Generate pixel-perfect worlds from real elevation data, with dynamic tile loading and customizable terrain shaping.  

---

## 🌐 Terrain Data Pipeline
- **Source**: AWS S3 (`elevation-tiles-prod/terrarium/{zoom}/{x}/{y}.png`)
- **Temeperature** Personal Source Hosted on GitHub, I compiled this using QGIS. (`clim-monthly/{month}/{zoom}/{x}/{y}`) **This is a temporary hosting solution**
- **Pixel-to-Block**: 1 pixel = 1 Minecraft block.  
- **Tile System**:  
  - Each tile = `256×256` pixels (`256×256` blocks ingame).  
  - `zoom` level determines tile count per axis (`2^zoom` tiles).  
    - Example: `zoom: 8` = `2⁸ = 256` tiles → `256×256 = 65,536` blocks wide.  

---

## 🗃️ Downloads
- While it is still at Alpha quality, you can download indev test versions [here](https://nightly.link/ly-nxs/terrarium/workflows/build/1.21-World/Artifacts)
- Support is not guarunteed
- BlossomLib is required, and FabricAPI
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

- Taken with v0.0.2-beta.1 + Conquest Reforged + Photon/UShader

**Zoom: 13 | Height 768**

---
![2025-04-10_21 09 04](https://github.com/user-attachments/assets/4e0a9457-8c38-4ad0-b42a-ed6bfe8eaa2a)
![2025-04-10_21 24 04](https://github.com/user-attachments/assets/993261b0-c90d-49b9-bdf3-25490cd9e072)
![2025-04-10_21 25 46](https://github.com/user-attachments/assets/4e1b4750-0589-4ce5-aae1-514ee7a88f21)

---
## 🛠️ How It Works
- Tile Fetching: Downloads 256×256 PNG tiles from AWS based on zoom and caches.
- Height Mapping: Converts RGB pixels to block heights, scaled by additionalAlt.
- Biome Placement: Vanilla biomes mapped using elevation (startingY + altitudeDropoff).

## 🔧 **Configuration**  
`/config/BlossomMods/Terrarium.json`:  
```json
{   
  "zoom": 13,   // 2¹³ tiles = 8,192 tiles wide (≈2M blocks at 256px/tile).
  "worldHeight": 768,       // Max Y height.
  "startingY": 0,           // Base height offset (negative for deeper oceans).
  "adjustXoffset": 400000, // Where on the map the world should generate: spawn location essentially!
  "adjustZoffset": 800000,
  "ELEVATION_URL": "https://s3.amazonaws.com/elevation-tiles-prod/terrarium/", //elevation data source
  "TEMPERATURE_URL": "https://raw.githubusercontent.com/ly-nxs/terrarium-data/refs/heads/main/tiles/climate-monthly/", //climate data source
  "CACHE_DIR": "./tiles", //tile cache dir
  "month": 0 //month for climate data, currently only january - 0, and february - 1
}
