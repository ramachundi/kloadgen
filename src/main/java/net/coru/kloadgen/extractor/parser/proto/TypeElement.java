package net.coru.kloadgen.extractor.parser.proto;

import java.util.List;

public interface TypeElement {
  String name();
  String qualifiedName();
  String documentation();
  List<OptionElement> options();
  List<TypeElement> nestedElements();
  String toSchema();
}
