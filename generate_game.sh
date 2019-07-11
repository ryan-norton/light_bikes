#!/bin/bash
CREATE_URL="http://light-bikes.inseng.net/games?numPlayers=2"
# CREATE_URL="http://localhost:8080/games?numPlayers=2"
for i in "$@"
do
case $i in
  -q|--qualify)
  echo "Creating a qualification game..."
  CREATE_URL="$CREATE_URL&addServerBot=true&serverBotDifficulty=1"
  shift
  ;;
  -h|--help)
  echo "Add -q or --qualify to create a qualification game"
  exit 0
  ;;
  *)
  echo "unknown option"
  exit 1
  ;;
esac
done

GAME_ID="$(curl -s -X POST "$CREATE_URL" -H "accept: */*" | jq -r '.id')"
echo "Game id is: $GAME_ID"

if [ -x "$(command -v pbcopy)" ]; then
  echo "$GAME_ID" | pbcopy
  echo "Copied to your clipboard!"
fi
