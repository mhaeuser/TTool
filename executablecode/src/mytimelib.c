#include<time.h>

#include "mytimelib.h"
#include "random.h"
#include "debug.h"

void addTime(struct timespec *src1, struct timespec *src2, struct timespec *dest) {
  dest->tv_nsec = src1->tv_nsec + src2->tv_nsec;
  dest->tv_sec = src1->tv_sec + src2->tv_sec;
  if (dest->tv_nsec > 1000000000) {
    dest->tv_sec = dest->tv_sec + (dest->tv_nsec / 1000000000);
    dest->tv_nsec = dest->tv_nsec % 1000000000;
  }

}

int isBefore(struct timespec *src1, struct timespec *src2) {
  if (src1->tv_sec > src2->tv_sec) {
    return 0;
  }

  if (src1->tv_sec < src2->tv_sec) {
    return 1;
  }

  if (src1->tv_nsec < src2->tv_nsec) {
    return 1;
  }

  return 0;
}

void minTime(struct timespec *src1, struct timespec *src2, struct timespec *dest) {
  if (isBefore(src1,src2)) {
    dest->tv_nsec = src1->tv_nsec;
    dest->tv_sec = src1->tv_sec;
  } else {
    dest->tv_nsec = src2->tv_nsec;
    dest->tv_sec = src2->tv_sec;
  }
  
}


void delayToTimeSpec(struct timespec *ts, long delay) {
  ts->tv_nsec = (delay % 1000000)*1000;
  ts->tv_sec = (delay / 1000000);
}

void waitFor(long minDelay, long maxDelay) {
  struct timespec tssrc;
  struct timespec tsret;
  int delay;

  delay = computeLongRandom(minDelay, maxDelay);

  debugLong("random delay=", delay);

  delayToTimeSpec(&tssrc, delay);

  debugLong("............. waiting For", delay);
  nanosleep(&tssrc, &tsret);
}

