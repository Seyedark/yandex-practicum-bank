package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should exchange"
    request {
        method GET()
        url "/exchange/exchange"
    }
    response {
        body([
                [
                        currency    : "USD",
                        purchaseRate: 2,
                        sellingRate : 1,
                ]
        ])
        status OK()
    }
}