package net.coru.kloadgen.extractor.parser.protobuf;

import org.antlr.v4.runtime.CharStream;

public class SampleProtoLexer {
  SampleExceptionHandler handler;

  public SampleProtoLexer() {}

  public SampleProtoLexer(CharStream input) {
    super(input, new RecognizerSharedState());
  }

  public SampleProtoLexer(CharStream input, RecognizerSharedState state) {
    super(input,state);
  }

  public void registerExceptionHandler(SampleExceptionHandler handler) {
    this.handler = handler;
  }

  public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    handler.handle(tokenNames, e);
  }
}
