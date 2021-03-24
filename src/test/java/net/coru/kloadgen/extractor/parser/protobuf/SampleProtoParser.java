package net.coru.kloadgen.extractor.parser.protobuf;

import groovyjarjarantlr4.runtime.RecognizerSharedState;
import org.antlr.v4.runtime.*;
import net.coru.kloadgen.extractor.parser.protobuf.ProtoParser.g4;
public class SampleProtoParser extends Per {
  SampleExceptionHandler handler;

  public SampleProtoParser(TokenStream input) {
    super(input, new RecognizerSharedState());
  }

  public SampleProtoParser(TokenStream input, RecognizerSharedState state) {
    super(input, state);
  }

  public void registerExceptionHandler(SampleExceptionHandler handler) {
    this.handler = handler;
  }

  public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    handler.handle(tokenNames, e);
  }
}
