# Tubes1_Heavymetal
Tugas Besar I IF2211 Strategi Algoritma Semester II Tahun 2021/2022 Pemanfaatan Algoritma Greedy dalam Aplikasi Permainan “Overdrive”

## Algoritma Greedy
Algoritma greedy yang dimplementasikan memprioritaskan FIX apabila damage yang dimiliki lebih dari 2 untuk menghindari rintangan.
Jika tidak perlu FIX maka mobil algoritma akan memprioritaskan menghindari rintangan.
Hal ini dilakukan dengan cara menghitung nilai prioritas pada masing-masinglane, depan, kiri, maupun kanan.
Kemudian memilih lane yang memiliki obstacles dengan nilai terkecil.
Jika jalan lancar maka algoritma akan menggunakan powerups yang dimiliki.
Jika tidak memiliki powerups maka akan melakukan accelerate.
Jika sudah kecepatan maksimum maka tidak akan melakukan apa-apa.

## Environment Requirements
* Java (minimal Java 8) : [Download](https://www.oracle.com/java/technologies/downloads/#java8)
* IntelIiJ IDEA : [Download](https://www.jetbrains.com/idea/)
* NodeJS: [Download](https://nodejs.org/en/download/)

## Setup
* Pastikan semua requirement di atas sudah terinstall pada perangkat keras yang akan digunakan.
* Perhatikan bahwa Game Engine yang diunduh dari link di atas merupakan starter pack yang digunakan oleh pemain untuk memulai membuat bot.
* Struktur folder starter pack tersebut dapat dilihat di https://github.com/EntelectChallenge/2020-Overdrive.
* Lakukan pengimplementasian kode program menggunakan Intellij IDEA (dapat dilakukan dengan menjalankan file pom.xml).
* Setelah diimplementasikan, lakukan instalasi program dengan menggunakan Maven Toolbox pada bagian Lifecycle yang terletak di bagian kanan Intellij IDEA.
* Instalasi ini menghasilkan sebuah folder bernama target yang akan berisi sebuah file bernama java-sample-bot-jar-with-dependencies.jar.
* Pindahkan file ini ke dalam folder starter-pack. Jika sudah ada, file yang lama bisa digantikan dengan file yang baru ini.
* Pastikan konfigurasi program yang ada di game-runner-config.json sudah benar, meliputi direktori bot yang digunakan.
* Jika menggunakan file yang terdapat dalam repositori ini, maka yang perlu dilakukan adalah menggantikan file jar dengan file yang ada di folder bin.
* Selain itu, jangan lupa untuk tetap mengubah source code program dengan mengganti folder starter-bots dengan folder src yang ada di repositori ini.
* Pada starter-pack, edit file `game-runner-config.json` sehingga `player-a` mengarah pada directory `bot.json`.
* Lalu run file `run.bat` pada windows.

## Authors
| Nama | NIM | Email |
| ----- | --- | ----------|
|Muhammad Akmal Arifin | 13520037 | <13520037@std.stei.itb.ac.id> |
|Azmi Alfatih Shalahuddin | 13520158 | <13520158@std.stei.itb.ac.id> |
|Daffa Romyz Aufa | 13520162 | <13520162@std.stei.itb.ac.id> |

## Referensi
    Munir, R., 2022. Algoritma Greedy (Bagian 1). [online] Informatika.stei.itb.ac.id. Available at: <https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2020-2021/Algoritma-Greedy-(2021)-Bag1.pdf> [Accessed 18 February 2022].  
    Munir, R., 2022. Algoritma Greedy (Bagian 2). [online] Informatika.stei.itb.ac.id. Available at: <https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2020-2021/Algoritma-Greedy-(2021)-Bag2.pdf> [Accessed 18 February 2022].  
    Munir, R., 2022. Algoritma Greedy (Bagian 3). [online] Informatika.stei.itb.ac.id. Available at: <https://informatika.stei.itb.ac.id/~rinaldi.munir/Stmik/2021-2022/Algoritma-Greedy-(2022)-Bag3.pdf> [Accessed 18 February 2022].  


