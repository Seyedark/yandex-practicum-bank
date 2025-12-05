package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should block"
    request {
        method GET()
        url "/blocker/block"
    }
    response {
        body([
                blocked: true
        ])
        headers {
            contentType(applicationJson())
        }
        status OK()
    }
}