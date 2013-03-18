package compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * js,jsx,css自动化压缩合并插件
 * 
 * @author fangzhibin
 * @goal compress
 * 
 */
public class CompressUtils extends AbstractMojo {

	/**
	 * @parameter expression="${compress.compresses}"
	 */
	private List<Compress> compresses;
	/**
	 * @parameter expression="${compress.baseSrc}"
	 */
	private String baseSrc;
	/**
	 * @parameter expression="${compress.baseDest}"
	 */
	private String baseDest;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (null == compresses || null == baseSrc || null == baseDest)
			return;
		try {
			for (Compress compress : compresses) {
				doCompress(compress, baseSrc, baseDest);
			}
		} catch (IOException e) {
			getLog().error(e);
		}
	}

	private void doCompress(Compress compress, String baseSrc, String baseDest) throws IOException {
		String includes = compress.getIncludes().trim();
		if (null != includes && includes.length() > 0) {
			String[] includeArr = includes.split(",");
			String[] srcArr = new String[includeArr.length];
			for (int i = 0; i < includeArr.length; i++) {
				srcArr[i] = baseSrc + includeArr[i].trim();
			}
			if ("jsx".equals(compress.getType().trim())) {
				compressJs(srcArr, baseDest + "/" + compress.getId().trim() + "." + compress.getType().trim(), true);
			} else if ("js".equals(compress.getType().trim())) {
				compressJs(srcArr, baseDest + "/" + compress.getId().trim() + "." + compress.getType().trim(), false);
			} else {
				compressCss(srcArr, baseDest + "/" + compress.getId().trim() + "." + compress.getType().trim());
			}

		}
	}

	public void compressJs(String[] src, String dest, boolean type) throws IOException {
		File[] srcF = new File[src.length];
		File destF = new File(dest);
		for (int i = 0; i < src.length; i++)
			srcF[i] = new File(src[i]);
		compressJs(srcF, destF, type);
	}

	public void compressJs(File[] src, File dest, boolean type) throws IOException {
		getLog().info("开始压缩jsx/js文件...");
		if (null != src && src.length > 0) {
			JavaScriptCompressor compressor;
			Reader reader;
			if (dest.exists()) {
				dest.delete();
			} else {
				if(dest.isDirectory()) {
					dest.mkdirs();
				} else {
					String path = dest.getAbsolutePath();
					path = path.substring(0, path.lastIndexOf(File.separator));
					File file = new File(path);
					file.mkdirs();
				}
			}
			dest.createNewFile();
			Writer writer = new OutputStreamWriter(new FileOutputStream(dest, true), "UTF-8");
			writer.append("/** " + new Date() + " **/\n");
			for (int i = 0; i < src.length; i++) {
				final String s = src[i].getName();
				if (type && src[i].getName().endsWith(".js")) {
					writer.append("<#noparse>");
				}
				writer.append("/** " + src[i].getName() + " **/");
				reader = new InputStreamReader(new FileInputStream(src[i]), "UTF-8");
				compressor = new JavaScriptCompressor(reader, new ErrorReporter() {
					@Override
					public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
						/*if (line < 0)
							getLog().error(s + " : " + message);
						else
							getLog().error(s + " : " + line + ':' + lineOffset + ':' + message);*/
					}

					@Override
					public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
						error(message, sourceName, line, lineSource, lineOffset);
						return new EvaluatorException(message);
					}

					@Override
					public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
						if (line < 0)
							getLog().error(s + " : " + message);
						else
							getLog().error(s + " : " + line + ':' + lineOffset + ':' + message);
					}
				});
				compressor.compress(writer, -1, true, true, true, true);
				if (type && src[i].getName().endsWith(".js")) {
					writer.append("</#noparse>");
				}
				writer.append("\n");
				reader.close();
			}
			getLog().info(dest.getName() + "文件压缩到" + dest.getAbsolutePath() + "目录下");
			writer.close();
		}
		getLog().info("jsx/js文件压缩完成.");
	}

	public void compressCss(String[] src, String dest) throws IOException {
		File[] srcF = new File[src.length];
		File destF = new File(dest);
		for (int i = 0; i < src.length; i++)
			srcF[i] = new File(src[i]);
		compressCss(srcF, destF);
	}

	private void compressCss(File[] src, File dest) throws IOException {
		getLog().info("开始压缩css文件...");
		if (null != src && src.length > 0) {
			CssCompressor compressor;
			Reader reader;
			if (dest.exists()) {
				dest.delete();
			} else {
				if(dest.isDirectory()) {
					dest.mkdirs();
				} else {
					String path = dest.getAbsolutePath();
					path = path.substring(0, path.lastIndexOf(File.separator));
					File file = new File(path);
					file.mkdirs();
				}
			}
			dest.createNewFile();
			Writer writer = new OutputStreamWriter(new FileOutputStream(dest, true), "UTF-8");
			writer.append("/** " + new Date() + " **/\n");
			for (int i = 0; i < src.length; i++) {
				writer.append("/** " + src[i].getName() + " **/");
				reader = new InputStreamReader(new FileInputStream(src[i]), "UTF-8");
				compressor = new CssCompressor(reader);
				compressor.compress(writer, -1);
				writer.append('\n');
				reader.close();
			}
			getLog().info(dest.getName() + "文件压缩到" + dest.getAbsolutePath() + "目录下");
			writer.close();
		}
		getLog().info("css文件压缩完成.");
	}
}
