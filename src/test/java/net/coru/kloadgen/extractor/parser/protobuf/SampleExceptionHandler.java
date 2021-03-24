package net.coru.kloadgen.extractor.parser.protobuf;

import java.util.Map;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.RecognitionException;

public class SampleExceptionHandler {
  private boolean isLexer;
  private String fname;
  private String[] tokenNames;
  private Map literalNames;

  public SampleExceptionHandler(String fname, Map literalNames, String[] tokenNames, boolean isLexer) {
    this.fname = fname;
    this.literalNames = literalNames;
    this.tokenNames = tokenNames;
    this.isLexer = isLexer;
  }


  private String handleNoViableAltException(NoViableAltException e) {
    if (isLexer) {
      return String.format("Unrecognized input '%c'.", e.toString());
    } else {
      return String.format("Unrecognized input '%s'.", e.getStartToken());
    }
  }


  public void handle(String[] tokenNames, RecognitionException e) {
    String errorMsg = "";
   if (e instanceof NoViableAltException) {
      errorMsg = this.handleNoViableAltException((NoViableAltException) e);
    }
    System.out.printf("[%s] Line %d: %s\n", this.fname, e.line, errorMsg);
  }
}
