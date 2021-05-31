#!/usr/bin/env bash
set -e -x

(
    #### Step 0: sanity ####
    printf "Step 0: Sanity check..."
    KOSA=$(dirname "$0")/..
    if [ "$KOSA" != "./bin/.." ]
    then
        printf "####"
        printf "'copy-txt-files.sh' must be run from Kosa root. Exiting."
        exit 1
    fi

    #### Step 1: get RSS repo from github ####
    printf "Step 1: Get RSS repo from github..."
    printf "Leaving Kosa to git clone..."
    # TODO: use /tmp instead?
    pushd $(dirname "$0")/../..
    if [ -d "daily_emails_rss_auto" ]
    then
        pushd daily_emails_rss_auto
        git pull
        popd
    else
        git clone git@github.com:pariyatti/Daily_emails_RSS.git daily_emails_rss_auto
    fi
    popd
    printf "...finished with git."

    printf "Still in Kosa root? Check:"
    pwd

    #### Step 2: copy txt files ####
    printf "Step 2: Copy TXT files..."
    mkdir -p $KOSA/txt/pali
    cp ../daily_emails_rss_auto/pwad/pali_words_one_loop.txt $KOSA/txt/pali/pali_words_one_loop.txt

    mkdir -p $KOSA/txt/buddha
    cp ../daily_emails_rss_auto/dwob/daily_words_one_loop.txt         $KOSA/txt/buddha/daily_words_one_loop.txt
    cp ../daily_emails_rss_auto/dwob/daily_words_one_loop_espanol.txt $KOSA/txt/buddha/daily_words_one_loop_es.txt
    cp ../daily_emails_rss_auto/dwob/daily_words_one_loop_fr.txt      $KOSA/txt/buddha/daily_words_one_loop_fr.txt
    cp ../daily_emails_rss_auto/dwob/daily_words_one_loop_it.txt      $KOSA/txt/buddha/daily_words_one_loop_it.txt
    cp ../daily_emails_rss_auto/dwob/daily_words_one_loop_pt-br.txt   $KOSA/txt/buddha/daily_words_one_loop_pt.txt
    cp ../daily_emails_rss_auto/dwob/daily_words_one_loop_sr.txt      $KOSA/txt/buddha/daily_words_one_loop_sr.txt
    cp ../daily_emails_rss_auto/dwob/daily_words_one_loop_chinese.txt $KOSA/txt/buddha/daily_words_one_loop_zh.txt

    mkdir -p $KOSA/txt/dohas
    cp ../daily_emails_rss_auto/dohas/daily_dohas_one_loop.txt            $KOSA/txt/dohas/daily_dohas_one_loop.txt
    cp ../daily_emails_rss_auto/dohas/daily_dohas_one_loop_lithuanian.txt $KOSA/txt/dohas/daily_dohas_one_loop_lt.txt
    cp ../daily_emails_rss_auto/dohas/daily_dohas_one_loop_pt-br.txt      $KOSA/txt/dohas/daily_dohas_one_loop_pt.txt
    cp ../daily_emails_rss_auto/dohas/daily_dohas_one_loop_chinese.txt    $KOSA/txt/dohas/daily_dohas_one_loop_zh.txt
)
