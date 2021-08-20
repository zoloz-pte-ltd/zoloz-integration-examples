set TITLE (git log -1 --oneline --format=%s | sed 's/^.*: //')
set WHERENOW (pwd)
git clone https://zolozAdmin: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@github.com/zoloz-pte-ltd/zoloz-integration-examples.git /tmp/tmpCode
rm -rf /tmp/tmpCode/*
cp -R -n * /tmp/tmpCode
cd /tmp/tmpCode
git add .
git commit -m "$TITLE"
git push origin
cd $WHERENOW
rm -rf /tmp/tmpCode
