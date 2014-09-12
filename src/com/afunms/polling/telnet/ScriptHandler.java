

package com.afunms.polling.telnet;

import java.util.Vector;


public class ScriptHandler {

  /** debugging level */
  private final static int debug = 0;

  private int matchPos;		// current position in the match
  private byte[] match;		// the current bytes to look for
  private boolean done = true;	// nothing to look for!


  public void setup(String match) {
    if(match == null) return;
    this.match = match.getBytes();
    matchPos = 0;
    done = false;
  }


  public boolean match(byte[] s, int length) {
    if(done) return true;
    for(int i = 0; !done && i < length; i++) {
      if(s[i] == match[matchPos]) {

        if(++matchPos >= match.length) {
          done = true;
          return true;
        }
      } else
        matchPos = 0; // get back to the beginning
    }
    return false;
  }
}
