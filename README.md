<div align="center">

# 🐾 Stretchy Cat 🐾
**Puzzle Meong Penuh Warna**

<img width="480" height="1024" alt="image" src="https://github.com/user-attachments/assets/9fd8a3af-562e-436c-8aaa-957c2bc5364e" />

[![Status](https://img.shields.io/badge/Status-Active-success.svg)]()
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-lightgrey.svg)]()
[![License](https://img.shields.io/badge/License-MIT-blue.svg)]()

</div>

---

## 📖 Tentang Game

**Stretchy Cat** adalah game teka-teki (puzzle) santai yang menggemaskan di mana kamu harus membantu seekor kucing meregangkan tubuhnya untuk memenuhi seluruh ruangan! Tarik kepala kucing, arahkan ke kotak-kotak yang kosong, dan selesaikan tantangannya. 

> *"Tarik kepalaku untuk mengisi ruangan ini, Meow! 😸"*

---

## ✨ Fitur Utama

*   🧩 **Teka-Teki Meregangkan Kucing:** Geser dan tarik kucing untuk mengisi semua ruang kosong di dalam papan puzzle.
*   🚧 **Hindari Rintangan!:** Papan permainan tidak selalu kosong! Hati-hati dan hindari berbagai rintangan seperti:
    *   🪴 Tanaman
    *   🐁 Tikus
    *   🧶 Gulungan Benang
*   🏆 **Kemajuan & Prestasi:** 
    *   Ratusan level yang menantang (Level 259, 260, dan seterusnya!).
    *   Selesaikan puzzle secepat mungkin (misal: dalam 20 detik) untuk mengumpulkan **3 Bintang** penuh!
    *   Lacak kemajuanmu dan terus maju ke tingkat berikutnya.
*   🎨 **Kustomisasi Kucing & Tema:** Buka *skin* dan tema baru untuk mengubah tampilan game agar tidak membosankan!

---

## 🎮 Cara Bermain

Sangat mudah dan intuitif untuk dimainkan!

1.  **Mulai:** Sentuh kepala kucing dari posisi awalnya.
2.  **Tarik/Geser:** Geser atau tarik kepala kucing ke petak kosong yang berdekatan.
3.  **Meregang:** Tubuh kucing akan otomatis meregang mengikuti jalur geseran/tarikanmu.
4.  **Menang:** Isi **semua** ruang kosong di papan tanpa menabrak rintangan untuk menyelesaikan level!

---

## 👗 Kustomisasi

Bosan dengan suasana yang itu-itu saja? Buka dan ganti tampilan kucing atau ubah suasana ruanganmu!

### 🐈 Pilihan Skin Kucing:
*   🟧 **Classic Orange** (Si Oren Klasik)
*   ⬛ **Tuxedo Hitam** (Elegan & Misterius)
*   🟤 **Caliko Belang** (Unik & Menggemaskan)

### 🖼️ Pilihan Tema Ruangan:
*   🛋️ **Ruang Keluarga** (Suasana rumah yang hangat)
*   🪻 **Kebun Lavender** (Tenang dan harum)
*   🚀 **Luar Angkasa** (Melayang di antara bintang-bintang)
*   🏝️ **Pantai Tropis** (Cerah dan menyegarkan)

---

## 🛠️ Instalasi & Build (Untuk Developer)

```bash
# Contoh Clone Repositori
git clone [https://github.com/Fauzanhandriansyah/stretchy-cat.git](https://github.com/Fauzanhandriansyah/stretchy-cat.git)

# Masuk ke direktori
cd stretchy-cat
## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
