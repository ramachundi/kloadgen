package net.coru.kloadgen.extractor.parser.proto;

import static net.coru.kloadgen.extractor.parser.proto.ProtoFile.Syntax.PROTO_2;
import static net.coru.kloadgen.extractor.parser.proto.ProtoFile.Syntax.PROTO_3;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.coru.kloadgen.extractor.parser.proto.ProtoParser.Context;
import net.coru.kloadgen.model.json.Field;
import net.coru.kloadgen.model.json.Schema;

public final class ProtoParserKload {

  private int pos;

  private int line;

  private int lineStart;

  private String packageName;

  private char[] data;

  private String filePath;

  private String prefix = "";

  private int column() {
    return pos - lineStart + 1;
  }

  private int line() {
    return line + 1;
  }

  public static Schema parse(String name, String data) {
    return new ProtoParserKload(name, data.toCharArray()).readProtoFile();
  }

  ProtoParserKload(String filePath, char[] data) {
    this.filePath = filePath;
    this.data = data;
  }

  Schema readProtoFile() {
    List<Field> fields = new ArrayList<>();
    Schema schema;
    String documentation = readDocumentation();
    Object declaration = readDeclaration(documentation, Context.FILE);
    return null;
  }

  private String readDocumentation() {
    String result = null;
    while (true) {
      skipWhitespace(false);
      if (pos == data.length || data[pos] != '/') {
        return result != null ? result : "";
      }
      String comment = readComment();
      result = (result == null) ? comment : (result + "\n" + comment);
    }
  }

  private void skipWhitespace(boolean skipComments) {
    while (pos < data.length) {
      char c = data[pos];
      if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
        pos++;
        if (c == '\n') newline();
      } else if (skipComments && c == '/') {
        readComment();
      } else {
        break;
      }
    }
  }

  private String readComment() {
    if (pos == data.length || data[pos] != '/') throw new AssertionError();
    pos++;
    int commentType = pos < data.length ? data[pos++] : -1;
    if (commentType == '*') {
      StringBuilder result = new StringBuilder();
      boolean startOfLine = true;

      for (; pos + 1 < data.length; pos++) {
        char c = data[pos];
        if (c == '*' && data[pos + 1] == '/') {
          pos += 2;
          return result.toString().trim();
        }
        if (c == '\n') {
          result.append('\n');
          newline();
          startOfLine = true;
        } else if (!startOfLine) {
          result.append(c);
        } else if (c == '*') {
          if (data[pos + 1] == ' ') {
            pos += 1; // Skip a single leading space, if present.
          }
          startOfLine = false;
        } else if (!Character.isWhitespace(c)) {
          result.append(c);
          startOfLine = false;
        }
      }
      throw unexpected("unterminated comment");
    } else if (commentType == '/') {
      if (pos < data.length && data[pos] == ' ') {
        pos += 1; // Skip a single leading space, if present.
      }
      int start = pos;
      while (pos < data.length) {
        char c = data[pos++];
        if (c == '\n') {
          newline();
          break;
        }
      }
      return new String(data, start, pos - 1 - start);
    } else {
      throw unexpected("unexpected '/'");
    }
  }

  private void newline() {
    line++;
    lineStart = pos;
  }

  private RuntimeException unexpected(String message) {
    throw new IllegalStateException(
        String.format("Syntax error in %s at %d:%d: %s", filePath, line(), column(), message));
  }

  private Object readDeclaration(String documentation, Context context) {
    // Skip unnecessary semicolons, occasionally used after a nested message declaration.
    if (peekChar() == ';') {
      pos++;
      return null;
    }

    String label = readWord();

    if (label.equals("package")) {
      if (!context.permitsPackage()) throw unexpected("'package' in " + context);
      if (packageName != null) throw unexpected("too many package names");
      packageName = readName();
      //fileBuilder.packageName(packageName);
      prefix = packageName + ".";
      if (readChar() != ';') throw unexpected("expected ';'");
      return null;
    } else if (label.equals("import")) {
      if (!context.permitsImport()) throw unexpected("'import' in " + context);
      String importString = readString();
      if ("public".equals(importString)) {
        //fileBuilder.addPublicDependency(readString());
      } else {
        //fileBuilder.addDependency(importString);
      }
      if (readChar() != ';') throw unexpected("expected ';'");
      return null;
    } else if (label.equals("syntax")) {
      if (!context.permitsSyntax()) throw unexpected("'syntax' in " + context);
      if (readChar() != '=') throw unexpected("expected '='");
      String syntax = readQuotedString();
      switch (syntax) {
        case "proto2":
          //fileBuilder.syntax(PROTO_2);
          break;
        case "proto3":
          //fileBuilder.syntax(PROTO_3);
          break;
        default:
          throw unexpected("'syntax' must be 'proto2' or 'proto3'. Found: " + syntax);
      }
      if (readChar() != ';') throw unexpected("expected ';'");
      return null;
    } else if (label.equals("option")) {
      //
      // OptionElement result = readOption('=');
      if (readChar() != ';') throw unexpected("expected ';'");
      return null;
    } else if (label.equals("message")) {
      //return readMessage(documentation);
    } else if (label.equals("enum")) {
      //return readEnumElement(documentation);
    } else if (label.equals("service")) {
      //return readService(documentation);
    } else if (label.equals("extend")) {
      //return readExtend(documentation);
    } else if (label.equals("rpc")) {
      if (!context.permitsRpc()) throw unexpected("'rpc' in " + context);
      //return readRpc(documentation);
    } else if (label.equals("required") || label.equals("optional") || label.equals("repeated")) {
      if (!context.permitsField()) throw unexpected("fields must be nested");
      FieldElement.Label labelEnum = FieldElement.Label.valueOf(label.toUpperCase(Locale.US));
      //return readField(documentation, labelEnum);
    } else if (label.equals("oneof")) {
      if (!context.permitsOneOf()) throw unexpected("'oneof' must be nested in message");
      // return readOneOf(documentation);
    } else if (label.equals("extensions")) {
      if (!context.permitsExtensions()) throw unexpected("'extensions' must be nested");
      //return readExtensions(documentation);
    } else if (context == Context.ENUM) {
      if (readChar() != '=') throw unexpected("expected '='");

      EnumConstantElement.Builder builder = EnumConstantElement.builder()
          .name(label)
          .tag(readInt());

      if (peekChar() == '[') {
        readChar();
        while (true) {
          //builder.addOption(readOption('='));
          char c = readChar();
          if (c == ']') {
            break;
          }
          if (c != ',') {
            throw unexpected("Expected ',' or ']");
          }
        }
      }
      if (readChar() != ';') throw unexpected("expected ';'");
      //documentation = tryAppendTrailingDocumentation(documentation);
      return builder.documentation(documentation).build();
    } else {
      throw unexpected("unexpected label: " + label);
    }
    return null;
  }

  private char peekChar() {
    skipWhitespace(true);
    if (pos == data.length) throw unexpected("unexpected end of file");
    return data[pos];
  }

  private int hexDigit(char c) {
    if (c >= '0' && c <= '9') return c - '0';
    else if (c >= 'a' && c <= 'f') return c - 'a' + 10;
    else if (c >= 'A' && c <= 'F') return c - 'A' + 10;
    else return -1;
  }

  //READ SECTION

  private String readName() {
    String optionName;
    char c = peekChar();
    if (c == '(') {
      pos++;
      optionName = readWord();
      if (readChar() != ')') throw unexpected("expected ')'");
    } else if (c == '[') {
      pos++;
      optionName = readWord();
      if (readChar() != ']') throw unexpected("expected ']'");
    } else {
      optionName = readWord();
    }
    return optionName;
  }

  private String readString() {
    skipWhitespace(true);
    return peekChar() == '"' ? readQuotedString() : readWord();
  }

  private String readQuotedString() {
    if (readChar() != '"') throw new AssertionError();
    StringBuilder result = new StringBuilder();
    while (pos < data.length) {
      char c = data[pos++];
      if (c == '"') return result.toString();

      if (c == '\\') {
        if (pos == data.length) throw unexpected("unexpected end of file");
        c = data[pos++];
        switch (c) {
          case 'a': c = 0x7; break;
          case 'b': c = '\b'; break;
          case 'f': c = '\f'; break;
          case 'n': c = '\n'; break;
          case 'r': c = '\r'; break;
          case 't': c = '\t'; break;
          case 'v': c = 0xb; break;
          case 'x':case 'X':
            c = readNumericEscape(16, 2);
            break;
          case '0':case '1':case '2':case '3':case '4':case '5':case '6':case '7':
            --pos;
            c = readNumericEscape(8, 3);
            break;
          default:
            // use char as-is
            break;
        }
      }

      result.append(c);
      if (c == '\n') newline();
    }
    throw unexpected("unterminated string");
  }

  private String readWord() {
    skipWhitespace(true);
    int start = pos;
    while (pos < data.length) {
      char c = data[pos];
      if ((c >= 'a' && c <= 'z')
          || (c >= 'A' && c <= 'Z')
          || (c >= '0' && c <= '9')
          || (c == '_')
          || (c == '-')
          || (c == '.')) {
        pos++;
      } else {
        break;
      }
    }
    if (start == pos) throw unexpected("expected a word");
    return new String(data, start, pos - start);
  }

  private char readNumericEscape(int radix, int len) {
    int value = -1;
    for (int endPos = Math.min(pos + len, data.length); pos < endPos; pos++) {
      int digit = hexDigit(data[pos]);
      if (digit == -1 || digit >= radix) break;
      if (value < 0) {
        value = digit;
      } else {
        value = value * radix + digit;
      }
    }
    if (value < 0) throw unexpected("expected a digit after \\x or \\X");
    return (char) value;
  }

  private char readChar() {
    char result = peekChar();
    pos++;
    return result;
  }

  private int readInt() {
    String tag = readWord();
    try {
      int radix = 10;
      if (tag.startsWith("0x") || tag.startsWith("0X")) {
        tag = tag.substring("0x".length());
        radix = 16;
      }
      return Integer.valueOf(tag, radix);
    } catch (Exception e) {
      throw unexpected("expected an integer but was " + tag);
    }
  }

}
