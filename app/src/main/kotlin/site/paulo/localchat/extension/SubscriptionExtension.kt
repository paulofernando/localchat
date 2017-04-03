package site.paulo.localchat.extension

import rx.Subscription
import rx.subscriptions.CompositeSubscription

fun Subscription.addTo(compositeSubscription: CompositeSubscription) {
    compositeSubscription.add(this)
}
