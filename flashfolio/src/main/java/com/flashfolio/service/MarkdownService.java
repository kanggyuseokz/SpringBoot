package com.flashfolio.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.springframework.stereotype.Service;

@Service
public class MarkdownService {

    // 파서와 렌더러는 한 번만 생성해서 재사용 (성능 최적화)
    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer  renderer = HtmlRenderer.builder().build();

    public String renderHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) return "";
        return renderer.render(parser.parse(markdown));
    }
}