namespace java com.github.chzh.example

service ThriftExampleService{
    string test(1:i32 id, 2:string message)
}