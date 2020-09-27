
$MAJOR=0
$MINOR=1
$PATCH=0

git log --pretty=format:"%(trailers:key=change-type,valueonly,only,unfold)" | ForEach-Object {
    if($_){
        switch -casesensitive ($_){
            "fix" {
                $PATCH++
            }
            "feature" {
                $MINOR++
                $PATCH=0
            }
            "break" {
                $MAJOR++
                $MINOR=0
                $PATCH=0
            }
        }
    }
}

$COMMIT=(git rev-parse --short HEAD) | Out-String

Write-Output "::set-env name=SPECIFICATION_VERSION::$MAJOR.$MINOR.$PATCH"
Write-Output "::set-env name=IMPLEMENTATION_VERSION::$MAJOR.$MINOR.$PATCH+$COMMIT"