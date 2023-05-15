package ch.usi.si.seart.src2abs;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Problem;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.experimental.FieldDefaults;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParseResult;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command(
		name = "src2abs",
		separator = " ",
		version = "1.0.0",
		mixinStandardHelpOptions = true,
		description = "Transforms source code into an equivalent abstract textual representation."
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Main implements Callable<Integer> {

	@SuppressWarnings({"unused", "FieldMayBeFinal"})
	@Parameters(
			index = "0",
			description = "Path to file containing the source code used as input."
	)
	Path input;

	@SuppressWarnings({"unused", "FieldMayBeFinal"})
	@Option(
			names = {"-o", "--output"},
			description =
					"Path to file which will contain the abstraction result. " +
					"If not specified, the abstraction result and mappings are printed to console."
	)
	Path output;

	@SuppressWarnings({"unused", "FieldMayBeFinal"})
	@Option(
			names = {"-i", "--idioms"},
			description = "Path to the file containing a newline-separated list of idioms."
	)
	Path idioms;

	@SuppressWarnings({"unused", "FieldMayBeFinal"})
	@Option(
			names = {"-g", "--granularity"},
			description =
					"The granularity level that abstraction will be performed on. " +
					"Can be one of: ${COMPLETION-CANDIDATES}. " +
					"Default: CLASS."
	)
	Parser.Granularity granularity = Parser.Granularity.CLASS;

	@Override
	public Integer call() throws Exception {
		if (Files.notExists(input))
			throw new NoSuchFileException(input.toString());

		Set<String> keywords;
		if (idioms == null) {
			keywords = Set.of();
		} else {
			@Cleanup Stream<String> lines = Files.lines(idioms);
			keywords = lines.collect(Collectors.toSet());
		}

		if (output == null) {
			Abstractor.abstractCode(granularity, input, keywords);
		} else {
			Path parent = output.getParent();
			if (parent != null && Files.notExists(parent)) {
				Files.createDirectories(parent);
			}
			Abstractor.abstractCode(granularity, input, output, keywords);
		}

		return 0;
	}

	public static void main(String[] args) {
		int code = new CommandLine(new Main())
				.setExecutionExceptionHandler(new ExecutionExceptionHandler())
				.setCaseInsensitiveEnumValuesAllowed(true)
				.execute(args);
		System.exit(code);
	}

	private static final class ExecutionExceptionHandler implements IExecutionExceptionHandler {
		@Override
		public int handleExecutionException(
				Exception ex, CommandLine commandLine, ParseResult parseResult
		) throws Exception {
			PrintWriter err = commandLine.getErr();
			int exitCode = commandLine.getCommandSpec().exitCodeOnExecutionException();
			if (ex instanceof NoSuchFileException) {
				String message = String.format(
						"Invalid value for required parameter '<input>': the file '%s' was not found", ex.getMessage()
				);
				err.println(message);
				commandLine.usage(err);
				return exitCode;
			} else if (ex instanceof ParseProblemException) {
				ParseProblemException ppex = (ParseProblemException) ex;
				err.println("Provided file could not be parsed, the following problems were reported:\n");
				ppex.getProblems().stream()
						.map(Problem::getMessage)
						.forEach(err::println);
				return exitCode;
			} else {
				throw ex;
			}
		}
	}
}
