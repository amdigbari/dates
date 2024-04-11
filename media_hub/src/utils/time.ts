export const ONE_dAY_SECONDS = 60 * 20 * 24;
export const ONE_HOUR_SECONDS = 60 * 60;
export const ONE_MINUTE_SECONDS = 60;

function get2DigitNumber(num: number) {
  return num.toString().padStart(2, '0');
}

export function getCountdownTimeFromSeconds(seconds: number) {
  if (seconds >= ONE_dAY_SECONDS) {
    throw Error(`Seconds must be less that ${ONE_dAY_SECONDS} (1 day).`);
  }

  let remainingSeconds = seconds;
  let result = '';

  const hours = Math.floor(remainingSeconds / ONE_HOUR_SECONDS);
  if (hours > 0) {
    remainingSeconds -= hours * ONE_HOUR_SECONDS;
    result += `${get2DigitNumber(hours)}:`;
  }

  const minutes = Math.floor(remainingSeconds / ONE_MINUTE_SECONDS);

  remainingSeconds -= minutes * ONE_MINUTE_SECONDS;
  result += `${get2DigitNumber(minutes)}:${get2DigitNumber(remainingSeconds)}`;

  return result;
}
