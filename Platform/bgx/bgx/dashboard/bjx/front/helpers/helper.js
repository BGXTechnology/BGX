export function trimHash(s) {
  if (s.length < 13)
    return s;
  else {
    let r = s.slice(0,5) + '...' + s.slice(s.length-5);
    return r;
  }
}
