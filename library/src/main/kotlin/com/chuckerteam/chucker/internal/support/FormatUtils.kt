package com.chuckerteam.chucker.internal.support

import com.chuckerteam.chucker.internal.data.entity.HttpHeader
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import graphql.language.AstPrinter
import graphql.parser.Parser
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXParseException
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.StringWriter
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.Locale
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.math.ln
import kotlin.math.pow

internal object FormatUtils {

    private const val SI_MULTIPLE = 1000
    private const val BASE_TWO_MULTIPLE = 1024

    private val gqlQueryRegex = Regex("""(?<="query":"|"query": ")(.*)(?=")""")
    private val gqlOperationNameRegex = Regex("""(?<="operationName":"|"operationName": ")(.+?)(?=")""")
    private val bracketOrSpaceRegex = Regex("""([^({\s]+)""")

    fun formatHeaders(httpHeaders: List<HttpHeader>?, withMarkup: Boolean): String {
        return httpHeaders?.joinToString(separator = "") { header ->
            if (withMarkup) {
                "<b> ${header.name}: </b>${header.value} <br />"
            } else {
                "${header.name}: ${header.value}\n"
            }
        } ?: ""
    }

    fun formatByteCount(bytes: Long, si: Boolean): String {
        val unit = if (si) SI_MULTIPLE else BASE_TWO_MULTIPLE

        if (bytes < unit) {
            return "$bytes B"
        }

        val exp = (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
        val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"

        return String.format(Locale.US, "%.1f %sB", bytes / unit.toDouble().pow(exp.toDouble()), pre)
    }

    fun formatJson(json: String): String {
        return try {
            val je = JsonParser.parseString(json)
            JsonConverter.instance.toJson(je)
        } catch (e: JsonParseException) {
            json
        }
    }

    fun formatXml(xml: String): String {
        return try {
            val documentFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
            // This flag is required for security reasons
            documentFactory.isExpandEntityReferences = false

            val documentBuilder: DocumentBuilder = documentFactory.newDocumentBuilder()
            val inputSource = InputSource(ByteArrayInputStream(xml.toByteArray(Charset.defaultCharset())))
            val document: Document = documentBuilder.parse(inputSource)

            val domSource = DOMSource(document)
            val writer = StringWriter()
            val result = StreamResult(writer)

            TransformerFactory.newInstance().apply {
                setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
            }.newTransformer().apply {
                setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
                setOutputProperty(OutputKeys.INDENT, "yes")
                transform(domSource, result)
            }
            writer.toString()
        } catch (ignore: SAXParseException) {
            xml
        } catch (ignore: IOException) {
            xml
        } catch (ignore: TransformerException) {
            xml
        }
    }

    fun formatUrlEncodedForm(form: String): String {
        return try {
            if (form.isBlank()) {
                return form
            }
            form.split("&").joinToString(separator = "\n") { entry ->
                val keyValue = entry.split("=")
                val key = keyValue[0]
                val value = if (keyValue.size > 1) URLDecoder.decode(keyValue[1], "UTF-8") else ""
                "$key: $value"
            }
        } catch (ignore: IllegalArgumentException) {
            form
        } catch (ignore: UnsupportedEncodingException) {
            form
        }
    }

    fun formatJsonWithGqlQuery(text: String): String {
        val json = formatJson(text)
        val matchResult = gqlQueryRegex.find(json)
        return if (matchResult != null) {
            val parser = Parser()
            val doc: graphql.language.Document? = try {
                parser.parseDocument(matchResult.value)
            } catch (e: Exception) {
                null
            }
            if (doc != null) {
                val formattedGql = AstPrinter.printAst(doc)
                json.replace(matchResult.value, formattedGql)
            } else {
                json
            }
        } else json
    }


    fun extractOperationName(json: String?): String? {
        return if (json == null) {
            null
        } else {
            val matchResult = gqlOperationNameRegex.find(json)
            matchResult?.value
        }
    }

    fun extractGqlRoot(text: String?, prefix: String = "\u0020\u0020\u0020"): String? {
        return if (text == null) {
            null
        } else {
            val matchResult = gqlQueryRegex.find(text)
            if (matchResult != null) {
                val gql = matchResult.value
                val gqlOp = bracketOrSpaceRegex.find(gql)
                val api = bracketOrSpaceRegex.find(gql.substringAfter('{', ""))
                "$prefix{${gqlOp?.value} {${api?.value}} }"
            } else null
        }
    }
}
