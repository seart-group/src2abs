package ch.usi.si.seart.src2abs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class Abstractor {

	@Getter
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	public static class Result {

		String abstracted;
		Map<String, String> mapping;

		private Result(Parser.Granularity granularity, String original, Set<String> idioms) {
			String cleaned = cleanCode(original);
			Parser parser = new Parser(granularity);
			parser.parse(cleaned);
			Tokenizer tokenizer = new Tokenizer(parser, idioms);
			this.abstracted = tokenizer.tokenize(cleaned);
			this.mapping = tokenizer.export();
		}

		public Collection<String> mappingKeys() {
			return this.mapping.keySet();
		}

		public Collection<String> mappingValues() {
			return this.mapping.values();
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder(abstracted);
			if (!mapping.isEmpty()) {
				builder.append('\n');
				builder.append('\n');
				mapping.forEach((key, value) ->
						builder.append(value)
								.append(' ')
								.append('=')
								.append(' ')
								.append(key)
								.append('\n')
				);
			}
			return builder.toString();
		}

		private static String cleanCode(String sourceCode) {
			Pattern pattern = Pattern.compile("(\".+\")");
			Matcher matcher = pattern.matcher(sourceCode);

			String group;
			String okGroup;
			while (matcher.find()) {
				for (int i = 0; i <= matcher.groupCount(); i++) {
					group = matcher.group(i);
					// okGroup = group.replaceAll("@", "<AT>");
					okGroup = group.replaceAll("//", "<DOUBLE_SLASH>");
					sourceCode = sourceCode.replace(group, okGroup);
				}
			}

			sourceCode = sourceCode.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", "");
			// sourceCode = sourceCode.replaceAll("@.+", "");

			return sourceCode;
		}
	}

	@SneakyThrows(IOException.class)
	public void abstractCode(
			Parser.Granularity granularity, Path input, Path output, Set<String> idioms
	) {
		String original = Files.readString(input);
		Result result = new Result(granularity, original, idioms);
		Files.write(output, result.getAbstracted().getBytes());
		Path mapping = output.resolveSibling(output.getFileName() + ".map");
		String keys = String.join(",", result.mappingKeys());
		String values = String.join(",", result.mappingValues());
		Files.write(mapping, List.of(keys, values));
	}

	@SneakyThrows(IOException.class)
	public void abstractCode(
			Parser.Granularity granularity, Path input, Set<String> idioms
	) {
		String original = Files.readString(input);
		Result result = new Result(granularity, original, idioms);
		System.out.print(result);
	}

	public Result abstractCode(
			Parser.Granularity granularity, String original, Set<String> idioms
	) {
		return new Result(granularity, original, idioms);
	}
}
