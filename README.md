# Task Manager Project - Pembelajaran Hari Ini & Persiapan Interview Senior Backend Developer

Hari ini, fokus pembelajaran kita adalah pada konfigurasi **GitHub Actions** melalui file `.github/workflows/ci.yml` dalam konteks proyek "Task Manager" ini. Memahami CI/CD (Continuous Integration/Continuous Deployment) adalah keterampilan krusial bagi seorang Senior Backend Developer.

## Memahami `ci.yml` dan GitHub Actions dari Perspektif Backend Developer

Sebagai seorang Senior Backend Developer, Anda tidak hanya menulis kode, tetapi juga bertanggung jawab atas keandalan, skalabilitas, dan proses deployment layanan backend Anda. File `ci.yml` adalah jantung dari otomatisasi ini.

**GitHub Actions** memungkinkan kita untuk:
1.  **Mengotomatisasi Build & Test Backend Services**: Setiap kali kode backend di-push, workflow dapat secara otomatis mengkompilasi kode (misalnya, Java/Kotlin dengan Gradle/Maven, Node.js dengan npm, Go dengan `go build`), menjalankan unit test, integration test, dan bahkan end-to-end test terhadap API backend. Ini memastikan bahwa setiap perubahan tidak merusak fungsionalitas yang sudah ada.
2.  **Menerapkan Static Code Analysis & Linting**: Workflow dapat menjalankan tools seperti SonarQube, ESLint, atau linters spesifik bahasa untuk memastikan kualitas kode, kepatuhan terhadap standar, dan mendeteksi potensi bug atau kerentanan keamanan sebelum deployment.
3.  **Manajemen Dependensi & Vulnerability Scanning**: Mengotomatisasi pemindaian dependensi untuk kerentanan (misalnya, Dependabot, Snyk) untuk menjaga keamanan rantai pasokan perangkat lunak.
4.  **Deployment Otomatis (CD)**: Setelah semua tes dan pemeriksaan kualitas lulus, workflow dapat secara otomatis mendeploy layanan backend ke lingkungan staging atau produksi (misalnya, ke server, Kubernetes cluster, AWS Lambda, Google Cloud Run). Ini mengurangi kesalahan manual dan mempercepat siklus rilis.
5.  **Notifikasi & Feedback Loop**: Memberikan notifikasi otomatis (misalnya, ke Slack, email) tentang status build dan deployment, memungkinkan tim bereaksi cepat terhadap masalah.

Memahami dan mampu merancang workflow CI/CD yang efektif adalah tanda seorang Senior Backend Developer yang matang.

## Pertanyaan & Jawaban Interview untuk Senior Backend Developer (Fokus CI/CD)

Berikut adalah beberapa pertanyaan yang mungkin diajukan dalam interview senior, beserta contoh jawaban yang menunjukkan pemahaman mendalam:

---

**Q1: "Sebagai Senior Backend Developer, mengapa CI/CD sangat penting untuk layanan backend yang Anda kembangkan?"**

**A1:** "CI/CD sangat fundamental karena beberapa alasan utama:
1.  **Peningkatan Kualitas & Keandalan**: Dengan otomatisasi build dan test pada setiap commit, kami dapat mendeteksi bug dan regresi lebih awal, bahkan sebelum kode mencapai lingkungan staging. Ini secara drastis mengurangi risiko masalah di produksi.
2.  **Percepatan Waktu Rilis (Time-to-Market)**: Proses deployment manual seringkali lambat dan rawan kesalahan. CI/CD memungkinkan rilis yang lebih sering dan lebih cepat, yang berarti fitur baru dapat sampai ke pengguna lebih cepat.
3.  **Mengurangi Risiko Deployment**: Dengan deployment otomatis dan teruji, risiko kegagalan saat deployment berkurang. Jika ada masalah, rollback juga bisa diotomatisasi atau setidaknya lebih mudah dilakukan.
4.  **Kolaborasi Tim yang Lebih Baik**: CI mendorong integrasi kode secara sering, mengurangi 'merge conflicts' besar dan memastikan semua orang bekerja dengan basis kode yang stabil.
5.  **Efisiensi Operasional**: Mengurangi beban kerja manual untuk tim DevOps atau SRE, memungkinkan mereka fokus pada tugas-tugas yang lebih kompleks seperti optimasi infrastruktur dan pemantauan.
Singkatnya, CI/CD adalah tulang punggung untuk membangun dan memelihara layanan backend yang tangguh dan responsif."

---

**Q2: "Bagaimana Anda akan merancang workflow GitHub Actions untuk layanan backend yang menggunakan microservices, dan apa saja pertimbangan utamanya?"**

**A2:** "Untuk microservices, saya akan merancang workflow GitHub Actions dengan pendekatan 'monorepo' atau 'polyrepo' tergantung struktur proyek.
Jika **monorepo**:
*   Saya akan menggunakan 'path filtering' (`on: push: paths:`) untuk memicu workflow hanya ketika perubahan terjadi pada direktori microservice tertentu. Ini menghindari build dan test yang tidak perlu untuk microservice lain.
*   Setiap microservice akan memiliki workflow terpisah atau job yang spesifik dalam satu workflow besar.

Pertimbangan utama:
1.  **Isolasi**: Setiap microservice harus memiliki pipeline build, test, dan deploy-nya sendiri yang terisolasi. Kegagalan di satu microservice tidak boleh menghentikan deployment microservice lain yang tidak terkait.
2.  **Testing Strategy**: Menerapkan unit tests, integration tests (termasuk interaksi antar microservices melalui mock atau test environment), dan contract testing (menggunakan Pact atau sejenisnya) untuk memastikan kompatibilitas API.
3.  **Deployment Strategy**: Menggunakan strategi deployment seperti Canary, Blue/Green, atau Rolling Updates untuk meminimalkan downtime dan risiko. GitHub Actions dapat mengintegrasikan dengan tools seperti Kubernetes, Helm, atau cloud provider CLI untuk ini.
4.  **Secrets Management**: Menggunakan GitHub Secrets untuk menyimpan kredensial sensitif (API keys, database passwords) yang dibutuhkan selama proses CI/CD, memastikan tidak ada hardcoding.
5.  **Observability**: Memastikan bahwa pipeline mencakup langkah-langkah untuk mengintegrasikan dengan sistem monitoring dan logging, sehingga kita bisa memverifikasi kesehatan layanan setelah deployment."

---

**Q3: "Apa saja tantangan umum yang Anda hadapi saat mengimplementasikan CI/CD untuk backend, dan bagaimana Anda mengatasinya?"**

**A3:** "Beberapa tantangan umum meliputi:
1.  **Waktu Eksekusi Pipeline yang Lambat**: Terutama dengan basis kode besar atau banyak tes. Mengatasinya dengan:
    *   Paralelisasi job/step.
    *   Caching dependensi (misalnya, `actions/cache`).
    *   Mengoptimalkan tes agar lebih cepat dan efisien.
    *   Menggunakan 'self-hosted runners' jika ada kebutuhan resource yang sangat spesifik atau besar.
2.  **Flaky Tests**: Tes yang kadang berhasil, kadang gagal tanpa perubahan kode. Ini merusak kepercayaan pada pipeline. Solusinya adalah mengidentifikasi dan memperbaiki akar masalah tes tersebut, seringkali terkait dengan dependensi eksternal, kondisi balapan, atau data test yang tidak konsisten.
3.  **Manajemen Lingkungan (Environment Management)**: Memastikan lingkungan CI/CD konsisten dengan lingkungan staging/produksi. Menggunakan Docker untuk mengemas aplikasi dan dependensinya membantu mencapai konsistensi ini. Mengotomatisasi provisioning lingkungan test.
4.  **Keamanan (Security)**: Memastikan pipeline itu sendiri aman. Menggunakan 'least privilege' untuk token dan kredensial, memindai image Docker untuk kerentanan, dan memastikan tidak ada informasi sensitif yang terekspos di log.
5.  **Kompleksitas Konfigurasi**: File YAML bisa menjadi sangat kompleks. Menggunakan template, reusable workflows (fitur GitHub Actions), dan modularisasi konfigurasi dapat membantu.

Mengatasi tantangan ini membutuhkan kombinasi praktik terbaik, tooling yang tepat, dan iterasi berkelanjutan pada pipeline CI/CD."