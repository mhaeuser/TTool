#!/usr/bin/env bash
printf 'testing\n'
make noguitest

if [ $? -eq 0 ]; then
   printf 'SUCCESS\n'
   exit 0
fi

exit 1

