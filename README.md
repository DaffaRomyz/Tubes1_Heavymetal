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

## Running
Pada starter-pack, edit file **game-runner-config.json** sehingga **player-a** mengarah pada directori **bot.json**.
Lalu run file **run.bat** pada windows.

## Authors
>Muhammad Akmal Arifin - 13520037

>Azmi Alfatih Shalahuddin - 13520158

>Daffa Romyz Aufa - 13520162

