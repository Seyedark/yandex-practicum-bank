import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should change account password"
    request {
        method PATCH()
        url "/account/password"
        headers {
            contentType(applicationJson())
        }
        body([
                login   : "login",
                password: "12345",
        ])
    }
    response {
        status OK()
    }
}