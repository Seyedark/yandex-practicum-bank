package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should save exchange"
    request {
        method POST()
        url "/exchange/exchange"
        headers {
            contentType(applicationJson())
        }
        body([
                [
                        currency    : "USD",
                        purchaseRate: 2,
                        sellingRate : 1
                ]
        ])
    }
    response {
        status OK()
    }
}
