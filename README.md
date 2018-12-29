# Programmazione Concorrente e Distribuita 2018

Questo repository contiene le slide ed il codice di esempio discusso a lezione per il corso di Programmazione Concorrente e Distribuita per l'A.A. 2018 dell'Università di Padova, Facoltà di Matematica, Corso di Laurea di Informatica (Ordinamento 2011).

Il repository è così organizzato:

    README.md : questo file
    src : codice sorgente presentato a lezione
    build.gradle, settings.gradle : istruzioni per la compilazione con lo strumento (Gradle)[https://gradle.org]
    config : configurazioni per gli strumenti di analisi statica del codice
    slides : slide delle lezioni
    papers : documenti citati a lezione liberamente distribuibili o link interessanti.

Per predisporre l'esecuzione dei programmi d'esempio con l'IDE Eclipse, usare il comando `gradle eclipse`. Per usare l'IDE JIdea usare il comando `gradle idea`.

La maggior parte dei programmi eseguibili direttamente è richiamabile come un task specifico, per es. `gradle singleThreadPool`.

Le slide si possono consultare anche ai seguenti indirizzi:

    Lezione 13: [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson1.html]
                [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson1-nb.html]
    Lezione 14: [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson2.html]
                [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson2-nb.html]
    Lezione 15: [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson3.html]
                [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson3-nb.html]
    Lezione 16: [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson4.html]
                [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson4-nb.html]
    Lezione 17: [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson5.html]
                [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson5-nb.html]
    Lezione 18: [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson6.html]
                [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson6-nb.html]
    Lezione 19: [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson7.html]
                [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson7-nb.html]
    Lezione 20: [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson8.html]
                [http://pcd2018.s3-website.eu-central-1.amazonaws.com/lesson8-nb.html]

La versione `-nb` è priva dello sfondo, per una più facile stampa. Si può ottenere il layout per la stampa aggiungendo all'URL il parametro `?print-pdf`. Aggiungendo `?print-pdf&showNotes=true` si ottiene anche la sovraimpressione delle note per il presentatore.

# Laboratorio 1

Il laboratorio 1 è costituito da un insieme di classi di base che vanno completate per ottenere un risultato.

Il codice iniziale si trova nel package `pcd2018.lab1`. E' costituito da alcune classi prive di implementazione e relativi test.

Il tema dell'esercizio è la realizzazione di un calcolo parallelo che estragga delle statistiche riguardanti i migliori giocatori di Bowling e le migliori sale da Bowling degli USA.
I dati di partenza sono costituiti da circa 280.000 righe divise in sette file compressi con il compressore XZ.

L'obiettivo del sistema è implementare questa sequenza:

* leggere i file
* calcolare i punteggi
* attribuire i punteggi a giocatori e sale
* sommare, ordinare e calcolare le medie di punteggi e strike per partita
* ottenere i dieci migliori giocatori e le dieci migliori sale per media punteggio e strike per partita

Il seguente diagramma illustra la struttura di massima del sistema:

![diagramma](Lab1.png "Lab 1")

Il laboratorio consiste nel superare i test nell'ordine suggerito, per creare infine il programma completo.

## Classi

Le classi da completare sono le seguenti:

### `pcd2018.lab1.bowling.GameRecord`

Record in cui leggere i dati dai file di ingresso.

### `pcd2018.lab1.bowling.BowlingGame`

Classe che calcola il risultato di una partita di bowling per un giocatore. I test di questa classe sono contenuti nel package `pcd2018.lab1.bowling`. Il punteggio del bowling è un (kata classico)[http://codingdojo.org/kata/Bowling/]

### `pcd2018.lab1.Main`

Classe principale che regge tutto il calcolo. Va riempita a partire dal codice esistente, seguendo i suggerimenti dei commenti.

### `pcd2018.lab1.ScoreReader` (test: `pcd2018.lab1.ScoreReaderTest`)

La responsabilità di questa classe è di leggere un file (di chi gli viene dato il nome) e ottenere da ogni riga un record con i dati di gioco. La classe non è thread-safe, nè avvia thread: il controllo dell'esecuzione è a carico del chiamante.
Se il file è esaurito, il metodo `get()` deve ritornare `null`.

### `pcd2018.lab1.GameRecordToData` (test: `pcd2018.lab1.GameRecordToDataTest`)

Questa classe legge un record di dati di gioco ed estrae la chiave da usare per la sommatoria dei risultati, in modo da poter utilizzare lo stesso sommatore per più chiavi diverse. Si comporta come una funzione pura, non necessita di stato.

### `pcd2018.lab1.Decoder` (test: `pcd2018.lab1.DecoderTest`)

Questa classe è un `Runnable` che legge dalla coda sorgente e, attraverso i `GameRecordToData` ottenuti al momento della costruzione, propaga il risultato verso due diverse code di destinazione.

### `pcd2018.lab1.Summarizer` (test: `pcd2018.lab1.SummarizerTest`)

Questa classe è un `Runnable` che legge dalla coda indicata e somma i dati letti, secondo la chiave che contengono. A termine dell'elaborazione si può accedere alla mappa dei risultati.

Punti di attenzione:

* `ScoreReader` va pilotato da parte di un `Thread`. `Decoder` e `Summarizer` sono invece `Runnable`, quindi possono essere eseguiti direttamente da un `ExecutorService`
* Ci si aspetta che più `ScoreReader` vengano avviati contemporaneamente, mentre in un dato momento sarà in esecuzione un `Decoder` e due `Summarizer`.
* Il `Thread` che pilota lo `ScoreReader` dovrebbe chiudersi autonomamente quando lo `ScoreReader` ha esaurito il file.
* `Decoder` e `Summarizer` possono essere interrotti anche direttamente.

## Test

I test sono organizzati in passi, e sono "taggati" con apposite etichette per poterli eseguire singolarmente. Ogni etichetta è richiamabile da un comando `gradle`.

### Step 1: `gradle step1`

Vengono verificate la funzioni statiche di lettura di un `GameRecord` e di parsing di una riga di file di ingresso.

### Step 2: `gradle step2`

Oltre ai test del livello precedente, vengono verificata la lettura di un file di esempio da parte di `ScoreReader`

### Step 3: `gradle step3`

Oltre ai test precedenti, viene verificato il funzionamento della classe `Decoder`

### Step 4: `gradle step4`

Oltre ai test precedenti, viene verificato il funzionamento della classe `Summarizer`

Si consiglia di seguire l'ordine degli step, risolvere i test del livello e poi passare al livello successivo.

## Esecuzione

Il task `gradle lab1` esegue la classe Main e ottiene i risultati.

# Laboratorio 2 - Distribuzione

Il laboratorio 2 è costituito da un client ed un server che comunicano fra loro in una rete locale.

Il codice inziale è contenuto nel package `pcd2018.lab2`. Nel package `pcd2018.lab2.bowling` sono contenute delle classi di supporto.

Il tema dell'esercizio è la realizzazione di un totalizzatore che raccoglie i risultati delle partiti di bowling che si svolgono all'interno della stessa sala. Essendo la rete chiusa e gestita, e la comunicazione infrequente, si considera sufficiente la comunicazione via Datagram UDP, senza connessioni permanenti.

Il protocollo di comunicazione è costituito da messaggi nel formato "<lane>:<pins>" con _lane_ e _pins_ entrambi numeri interi. Per es: "4:5", "1:10", "7:0". _lane_ indica la pista che ha rilevato il tiro, e _pins_ il numero di birilli abbattuti. Il controllo della coerenza del risultato non è importante ed è comunque demandato alla classe che fa la totalizzazione (che viene fornita).

Non è richiesto, nel tema del laboratorio, un controllo o una gestione dell'inizio di una nuova partita sulla stessa _lane_ dopo il completamento della precendente. E' comunque una possibile estensione se il tempo lo permette o come esercizio.

## Package `pcd2018.lab2.bowling`

Questo package contiene due classi di utilità:

* la classe `BowlingScorer`, versione modificata di quella usata nel precedente laboratorio, permette di ottenere il punteggio progressivo via via che la partita prosegue.
* la classe `Bowler` genera una partita giocata da un giocatore di un livello fornito. Il parametro del livello regola la bontà dei tiri fatti. La classe si preoccupa di generare una partita valida.

## Classe `pcd2018.lab2.BowlingClient`

La classe `BowlingClient` è il client. Deve essere completata in modo che predisponga la comunicazione con il server ed invii una serie di risultati per una certa lane.

E' richiesto che la classe:

* generi una partita usando la classe `Bowler`, in modo da inviare ad ogni invocazione dati diversi
* legga dal parametro `args[0]` la _lane_ da inviare nel messaggio
* si concluda dopo aver inviato tutti i dati di una partita
* attenda qualche millisecondo (per es. 500) fra un invio e l'altro, così da poter provare il funzionamento di client concorrenti

La classe deve essere eseguita con il comando:

`./gradlew bowlingClient --args=4`

dove variando il parametro `args` varia anche la _lane_ emessa nei messaggi.

## Classe `pcd2018.lab2.BowlingServe`

La classe `BowlingServer` è il server. Deve essere completata in modo che:

* ascolti sulla porta indicata l'arrivo di pacchetti
* ne legga il contenuto per accodarlo ad una `BlockingQueue` di elaborazione

Nello stesso file è definita una classe `ScorePrinter` da completare, il cui scopo è:

* prendere da una `BlockingQueue` un dato da elaborare
* mantenere una mappa delle partite in corso indicizzata per _lane_
* creare una partita se non ancora iniziata per una _lane_
* aggiungere ad una partita iniziata un nuovo risultato preso dalla coda

Il server deve essere avviato con il comando:

`./gradlew bowlingServer`

Si consiglia di esegire con successo il test contenuto in `BowlingServerTest` come prima cosa. Quando il test è verde, procedere all'implementazione del codice di networking. Il test può essere lanciato con il comando `./gradlew bowlingTest`

Sono considerati argomento del laboratorio gli strumenti e le classi presentate fino alla lezione 19 - Primitive di Networking. E' perfettamente inutile alterare la classe di test fornita.

