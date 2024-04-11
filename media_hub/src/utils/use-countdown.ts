import { useCallback, useEffect, useRef, useState } from 'react';

interface CountdownReturn {
  time: number;
  start: () => void;
  pause: () => void;
  resume: () => void;
  reset: () => void;
  restart: () => void;
  counting: boolean;
}
export const useCountdown = (initialTime: number = 0, autoStart: boolean = false): CountdownReturn => {
  const [time, setTime] = useState(initialTime);
  const [isCounting, setCounting] = useState(false);
  const interval = useRef<NodeJS.Timeout>();

  const resetTimer = useCallback(() => {
    setCounting(false);
    clearInterval(interval.current);

    setTime(initialTime);
  }, [initialTime]);

  const startTimer = useCallback(() => {
    clearInterval(interval.current);

    setCounting(true);
    interval.current = setInterval(() => {
      setTime((time) => {
        const newTime = time - 1;

        if (newTime > 0) {
          return newTime;
        }

        resetTimer();
        return 0;
      });
    }, 1000); // Every 1 second
  }, [resetTimer]);

  const pauseTimer = useCallback(() => {
    setCounting(false);
    clearInterval(interval.current);
  }, []);

  const resumeTimer = useCallback(startTimer, [startTimer]);

  const restartTimer = useCallback(() => {
    resetTimer();

    startTimer();
  }, [resetTimer, startTimer]);

  useEffect(() => {
    autoStart && startTimer();

    return () => {
      resetTimer();
    };

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return {
    time,
    start: startTimer,
    pause: pauseTimer,
    restart: restartTimer,
    reset: resetTimer,
    resume: resumeTimer,
    counting: isCounting,
  };
};
