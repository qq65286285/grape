// package com.grape.grape.service;

// import com.grape.grape.entity.TestScenario;
// import com.grape.grape.service.impl.DocumentSplitterServiceImpl;

// import java.io.File;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.Future;
// /**
//  * 测试场景文档处理管道运行器
//  */
// public class PipelineRunner {

//     public static void main(String[] args) throws Exception {
//         // ========== 配置区 ==========
//         // 从配置文件或环境变量中读取配置
//         // 这里使用默认值，实际应用中应该从配置文件读取
//         String NEO4J_URI = System.getProperty("neo4j.uri", "bolt://localhost:7687");
//         String NEO4J_USER = System.getProperty("neo4j.username", "neo4j");
//         String NEO4J_PASSWORD = System.getProperty("neo4j.password", "12345678");
//         String DOCUMENT_DIR_PATH = System.getProperty("document.dir", "/path/to/your/test-scenarios");
//         File DOCUMENT_DIR = new File(DOCUMENT_DIR_PATH); // 测试场景文档目录路径

//         // ========== 初始化组件 ==========
//         DocumentSplitter splitter = new DocumentSplitter();
//         GraphTransformer graphWriter = new GraphTransformer(NEO4J_URI, NEO4J_USER, NEO4J_PASSWORD);

//         // ========== 并行处理所有文档 ==========
//         ExecutorService executor = Executors.newFixedThreadPool(4); // 根据CPU核数调整
//         List<Future<?>> futures = new ArrayList<>();

//         for (File doc : DOCUMENT_DIR.listFiles((dir, name) -> name.endsWith(".txt"))) {
//             futures.add(executor.submit(() -> {
//                 try {
//                     // Step1: 解析测试场景文档
//                     List<DocumentSplitter.TestScenario> scenarios = splitter.parseTestScenarios(doc);
//                     System.out.printf("Processing %d scenarios from %s...%n", scenarios.size(), doc.getName());

//                     // Step2: 将场景保存到图数据库
//                     if (!scenarios.isEmpty()) {
//                         graphWriter.saveTestScenarios(scenarios);
//                         System.out.printf("Saved %d scenarios from %s to Neo4j%n", scenarios.size(), doc.getName());
//                     }
//                 } catch (Exception e) {
//                     System.err.println("Error processing " + doc.getName() + ": " + e.getMessage());
//                     e.printStackTrace();
//                 }
//             }));
//         }

//         // 等待所有任务完成
//         for (Future<?> f : futures) f.get();
//         executor.shutdown();
//         graphWriter.close();
//         System.out.println("All test scenario documents processed successfully!");
//     }

//     // GraphTransformer 内部类定义
//     public static class GraphTransformer {
//         private final String uri;
//         private final String user;
//         private final String password;

//         public GraphTransformer(String uri, String user, String password) {
//             this.uri = uri;
//             this.user = user;
//             this.password = password;
//         }

//         public void saveTestScenarios(List<DocumentSplitter.TestScenario> scenarios) {
//             // 实现保存测试场景到 Neo4j 的逻辑
//             System.out.println("Saving " + scenarios.size() + " scenarios to Neo4j");
//         }

//         public void close() {
//             // 关闭 Neo4j 连接
//             System.out.println("Closing Neo4j connection");
//         }
//     }
// }
