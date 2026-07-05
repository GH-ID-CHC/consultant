package com.example.consultant.config;

import com.example.consultant.aiservice.RedisChatMemoryStore;
import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * ConsultantService类的配置类
 *
 * @author 柴和程
 * @date 2026/04/16
 */
@Configuration
public class CommonConfig {

    @Autowired
    private OpenAiChatModel model;
    
    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private RedisChatMemoryStore  chatMemoryStore;

    @Autowired
    private RedisEmbeddingStore redisEmbeddingStore;

    /*@Bean
    public ConsultantService consultantService(){
        ConsultantService cs = AiServices.builder(ConsultantService.class)
                .chatModel(model)
                .build();
        return cs;
    }*/

    /**
     * 聊天记忆
     *
     * @return {@link ChatMemory }
     */
    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory
                .builder()
                .maxMessages(20)
                .build();
    }

    /**
     * 聊天内存提供程序
     * 为了隔离不同请求的记忆
     * @return {@link ChatMemoryProvider }
     */
    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        ChatMemoryProvider chatMemoryProvider = new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory
                        .builder()
                        .id(memoryId)
                        //配置会话记忆对象
                        .chatMemoryStore(chatMemoryStore)
                        .maxMessages(20)
                        .build();
            }
        };
        return chatMemoryProvider;
    }

    /**
     * 构建向量数据库操作对象
     *
     * @return {@link EmbeddingStore }
     */
    @Bean
    public EmbeddingStore embeddingStore() {
        // 1. 加载文档进内存
        List<Document> documents = ClassPathDocumentLoader.loadDocuments("content",new ApachePdfBoxDocumentParser());
        // 2. 构建向量数据库对象 这里是内存向量数据库使用redisEmbeddingStore进行替换
//        InMemoryEmbeddingStore inMemoryChatMemoryStore = new InMemoryEmbeddingStore<>();
        // 文档分割器
        DocumentSplitter recursive = DocumentSplitters.recursive(500, 100);
        // 3. 完成文本的切割，向量化，存储
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor
                .builder()
                .embeddingStore(redisEmbeddingStore)
                .documentSplitter(recursive)
                .embeddingModel(embeddingModel)
                .build();
        ingestor.ingest(documents);
        return redisEmbeddingStore;

    }

    /**
     * 构建向量数据库检索对象
     *
     * @return {@link ContentRetriever }
     */
    @Bean
    public ContentRetriever contentRetriever(){
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(redisEmbeddingStore)
                .embeddingModel(embeddingModel)
                .minScore(0.5)
                .maxResults(3)
                .build();
    }
}
