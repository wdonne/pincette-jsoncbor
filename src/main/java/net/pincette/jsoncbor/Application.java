package net.pincette.jsoncbor;

import static com.fasterxml.jackson.dataformat.cbor.CBORFactory.builder;
import static javax.json.Json.createGenerator;
import static javax.json.Json.createParser;
import static net.pincette.io.StreamConnector.copy;
import static net.pincette.jf.Util.add;
import static net.pincette.util.Util.tryToDoRethrow;
import static net.pincette.util.Util.tryToDoWithRethrow;
import static net.pincette.util.Util.tryToGetWithSilent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import net.pincette.jf.JacksonGenerator;
import net.pincette.jf.JacksonParser;

/**
 * Reads from stdin and writes to stdout. If the input stream contains CBOR then JSON will be
 * produced. When it contains JSON then CBOR will be produced.
 *
 * @author Werner Donn\u00e9
 */
public class Application {
  private static boolean fromCbor(final InputStream in, final OutputStream out) {
    return tryToGetWithSilent(
            () -> new JacksonParser(builder().build().createParser(in)),
            parser ->
                tryToGetWithSilent(() -> createGenerator(out), generator -> add(parser, generator))
                    .orElse(null))
        .isPresent();
  }

  private static void fromJson(final InputStream in, final OutputStream out) {
    tryToDoWithRethrow(
        () -> createParser(in),
        parser ->
            tryToDoWithRethrow(
                () -> new JacksonGenerator(builder().build().createGenerator(out)),
                generator -> add(parser, generator)));
  }

  @SuppressWarnings("squid:S106") // Not logging.
  public static void main(final String[] args) {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();

    tryToDoRethrow(() -> copy(System.in, out));

    if (!fromCbor(new ByteArrayInputStream(out.toByteArray()), System.out)) {
      fromJson(new ByteArrayInputStream(out.toByteArray()), System.out);
    }
  }
}
