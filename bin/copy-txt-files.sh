#!/usr/bin/env bash
set -e

(
    #### Step 0: sanity ####
    printf "\nStep 0: Sanity check..."
    KOSA=$(dirname "$0")/..
    if [ "$KOSA" != "./bin/.." ]
    then
        printf "####\n"
        printf "'copy-txt-files.sh' must be run from Kosa root. Exiting.\n"
        exit 1
    fi
    printf "...sane.\n"

    #### Step 1: get RSS repo from github ####
    printf "\nStep 1: Get RSS repo from github...\n"
    printf "Leaving Kosa to git clone in /tmp...\n"
    pushd /tmp
    if [ -d "daily_emails_rss_auto" ]
    then
        pushd daily_emails_rss_auto
        pwd
        git pull
        popd
    else
        pwd
        git clone git@github.com:pariyatti/Daily_emails_RSS.git daily_emails_rss_auto
    fi
    popd
    printf "...finished with git.\n"

    printf "Still in Kosa root? Check:\n"
    pwd

    #### Step 2: copy txt files ####
    printf "\nStep 2: Copy TXT files...\n"
    RSS=/tmp/daily_emails_rss_auto

    printf "Copying Pali Word files...\n"
    mkdir -p $KOSA/txt/pali
    cp $RSS/pwad/pali_words_one_loop.txt $KOSA/txt/pali/pali_words_one_loop.txt

    printf "Copying Words of Buddha files...\n"
    mkdir -p $KOSA/txt/buddha
    cp $RSS/dwob/daily_words_one_loop.txt         $KOSA/txt/buddha/daily_words_one_loop.txt
    cp $RSS/dwob/daily_words_one_loop_espanol.txt $KOSA/txt/buddha/daily_words_one_loop_es.txt
    cp $RSS/dwob/daily_words_one_loop_fr.txt      $KOSA/txt/buddha/daily_words_one_loop_fr.txt
    cp $RSS/dwob/daily_words_one_loop_it.txt      $KOSA/txt/buddha/daily_words_one_loop_it.txt
    cp $RSS/dwob/daily_words_one_loop_pt-br.txt   $KOSA/txt/buddha/daily_words_one_loop_pt.txt
    cp $RSS/dwob/daily_words_one_loop_sr.txt      $KOSA/txt/buddha/daily_words_one_loop_sr.txt
    cp $RSS/dwob/daily_words_one_loop_chinese.txt $KOSA/txt/buddha/daily_words_one_loop_zh.txt

    printf "Copying Doha files...\n"
    mkdir -p $KOSA/txt/dohas
    cp $RSS/dohas/daily_dohas_one_loop.txt            $KOSA/txt/dohas/daily_dohas_one_loop.txt
    cp $RSS/dohas/daily_dohas_one_loop_lithuanian.txt $KOSA/txt/dohas/daily_dohas_one_loop_lt.txt
    cp $RSS/dohas/daily_dohas_one_loop_pt-br.txt      $KOSA/txt/dohas/daily_dohas_one_loop_pt.txt
    cp $RSS/dohas/daily_dohas_one_loop_chinese.txt    $KOSA/txt/dohas/daily_dohas_one_loop_zh.txt

    printf "...done.\n\n"

    #### Step 3: validate ####
    printf "\nStep 3: Validate that files were copied...\n"
    printf "Running 'tree txt'...\n"
    if command -v tree &> /dev/null
    then
        tree txt
    else
        printf "You do not have 'tree' installed.\n"
        printf "Install 'tree' and run 'tree txt' from this directory.\n"
    fi
    printf "You should see 3 directories, 12 TXT files, and 1 README.\n"
)
