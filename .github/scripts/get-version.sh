#!/bin/sh

./gradlew -Psnapshot=false properties --no-daemon --console=plain -q | grep "^version:" | awk '{printf $2}'