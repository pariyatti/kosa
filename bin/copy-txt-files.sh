#!/usr/bin/env bash
set -e

(
    #### Step 0: sanity ####
    printf "\nStep 0: Sanity check...\n"
    KOSA=$(dirname "$0")/..
    if [ "$KOSA" != "./bin/.." ]; then
        printf "####\n"
        printf "'copy-txt-files.sh' must be run from Kosa root. Exiting.\n"
        exit 1
    fi
    if [[ -z "${GIT_SSH_COMMAND}" ]]; then
        printf "\n####\n"
        printf "####\n"
        printf "WARNING: GIT_SSH_COMMAND is empty. \n"
        printf "Set GIT_SSH_COMMAND with: 'GIT_SSH_COMMAND=\"ssh -i ~/.ssh/id_rsa\"'.\n"
        printf "####\n"
        printf "####\n\n"
        printf "Attempting to set GIT_SSH_COMMAND now...\n"
        export GIT_SSH_COMMAND="ssh -i ~/.ssh/id_rsa"
    fi
    printf "...sane.\n"

    #### Step 1: add github.com fingerprint ####
    if grep -q "github.com" ~/.ssh/known_hosts; then
        echo "github.com fingerprint found in ~/.ssh/known_hosts already"
    else
        echo 'github.com ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQCj7ndNxQowgcQnjshcLrqPEiiphnt+VTTvDP6mHBL9j1aNUkY4Ue1gvwnGLVlOhGeYrnZaMgRK6+PKCUXaDbC7qtbW8gIkhL7aGCsOr/C56SJMy/BCZfxd1nWzAOxSDPgVsmerOBYfNqltV9/hWCqBywINIR+5dIg6JTJ72pcEpEjcYgXkE2YEFXV1JHnsKgbLWNlhScqb2UmyRkQyytRLtL+38TGxkxCflmO+5Z8CSSNY7GidjMIZ7Q4zMjA2n1nGrlTDkzwDCsw+wqFPGQA179cnfGWOWRVruj16z6XyvxvjJwbz0wQZ75XK5tKSb7FNyeIEs4TT4jk+S4dhPeAUC5y+bDYirYgM4GC7uEnztnZyaVWQ7B381AK4Qdrwt51ZqExKbQpTUNn+EjqoTwvqNj4kqx5QUCI0ThS/YkOxJCXmPUWZbhjpCg56i+2aB6CmK2JGhn57K5mj0MNdBXA4/WnwH6XoPWJzK5Nyu2zB3nAZp+S5hpQs+p1vN1/wsjk=' >>~/.ssh/known_hosts
    fi

    #### Step 2: get RSS repo from github ####
    printf "\nStep 1: Get RSS repo from github...\n"
    printf "Leaving Kosa to git clone in /tmp...\n"
    pushd /tmp
    if [ -d "daily_emails_rss_auto" ]; then
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
    cp $RSS/pwad/pali_words_one_loop.txt $KOSA/txt/pali/pali_words_one_loop_eng.txt
    cp $RSS/pwad/pali_words_one_loop_por.txt $KOSA/txt/pali/pali_words_one_loop_por.txt

    printf "Copying Words of Buddha files...\n"
    mkdir -p $KOSA/txt/buddha
    cp $RSS/dwob/daily_words_one_loop.txt $KOSA/txt/buddha/daily_words_one_loop_eng.txt
    cp $RSS/dwob/daily_words_one_loop_espanol.txt $KOSA/txt/buddha/daily_words_one_loop_spa.txt
    cp $RSS/dwob/daily_words_one_loop_fr.txt $KOSA/txt/buddha/daily_words_one_loop_fra.txt
    cp $RSS/dwob/daily_words_one_loop_it.txt $KOSA/txt/buddha/daily_words_one_loop_ita.txt
    cp $RSS/dwob/daily_words_one_loop_pt-br.txt $KOSA/txt/buddha/daily_words_one_loop_por.txt
    cp $RSS/dwob/daily_words_one_loop_sr.txt $KOSA/txt/buddha/daily_words_one_loop_srp.txt
    cp $RSS/dwob/daily_words_one_loop_chinese.txt $KOSA/txt/buddha/daily_words_one_loop_zho-hant.txt

    printf "Copying Doha files...\n"
    mkdir -p $KOSA/txt/dohas
    cp $RSS/dohas/daily_dohas_one_loop.txt $KOSA/txt/dohas/daily_dohas_one_loop_eng.txt
    cp $RSS/dohas/daily_dohas_one_loop_lithuanian.txt $KOSA/txt/dohas/daily_dohas_one_loop_lit.txt
    cp $RSS/dohas/daily_dohas_one_loop_pt-br.txt $KOSA/txt/dohas/daily_dohas_one_loop_por.txt
    cp $RSS/dohas/daily_dohas_one_loop_chinese.txt $KOSA/txt/dohas/daily_dohas_one_loop_zho-hant.txt

    printf "...done.\n\n"

    #### Step 4: validate ####
    printf "\nStep 3: Validate that files were copied...\n"
    printf "Running 'tree txt'...\n"
    if command -v tree &>/dev/null; then
        tree txt
    else
        printf "You do not have 'tree' installed.\n"
        printf "Install 'tree' and run 'tree txt' from this directory.\n"
    fi
    printf "You should see 3 directories, 13 TXT files, and 1 README.\n"
)
