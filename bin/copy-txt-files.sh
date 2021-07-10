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

    #### Step 1: add github.com fingerprint ####
    if grep -q "github.com" ~/.ssh/known_hosts
    then
        echo "github.com fingerprint found in ~/.ssh/known_hosts already"
    else
        echo 'github.com ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAq2A7hRGmdnm9tUDbO9IDSwBK6TbQa+PXYPCPy6rbTrTtw7PHkccKrpp0yVhp5HdEIcKr6pLlVDBfOLX9QUsyCOV0wzfjIJNlGEYsdlLJizHhbn2mUjvSAHQqZETYP81eFzLQNnPHt4EVVUh7VfDESU84KezmD5QlWpXLmvU31/yMf+Se8xhHTvKSCZIFImWwoG6mbUoWf9nzpIoaSjB+weqqUUmpaaasXVal72J+UX2B+2RPW3RcT0eOzQgqlJL3RKrTJvdsjE3JEAvGq3lGHSZXy28G3skua2SmVi/w4yCE6gbODqnTWlg7+wC604ydGXA8VJiS5ap43JXiUFFAaQ==' >> ~/.ssh/known_hosts
    fi

    #### Step 2: get RSS repo from github ####
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

    #### Step 3: copy txt files ####
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

    #### Step 4: validate ####
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
