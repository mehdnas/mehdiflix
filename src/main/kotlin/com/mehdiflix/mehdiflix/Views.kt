package com.mehdiflix.mehdiflix

class Views {

    interface GetSeries:
        SeriesViews.Title,
        SeriesViews.Description,
        SeriesViews.Creators,
        SeriesViews.Actors,
        SeriesViews.SeriesType,
        SeriesViews.Episode,
        SeriesViews.Seasons,
        SeriesViews.EpisodePrice,
        SeriesViews.Id,

        PersonViews.Name,
        PersonViews.Surname,
        PersonViews.SecondSurname,
        PersonViews.Id,

        SeasonViews.Number,
        SeasonViews.Episode,
        SeasonViews.Id,

        EpisodeViews.Number,
        EpisodeViews.Title,
        EpisodeViews.Description,
        EpisodeViews.Id


    interface PostSeries:
        SeriesViews.Title,
        SeriesViews.Description,
        SeriesViews.Creators,
        SeriesViews.Actors,
        SeriesViews.SeriesType,
        SeriesViews.Episode,
        SeriesViews.Seasons,

        PersonViews.Name,
        PersonViews.Surname,
        PersonViews.SecondSurname,

        SeasonViews.Number,
        SeasonViews.Episode,

        EpisodeViews.Number,
        EpisodeViews.Title,
        EpisodeViews.Description

    interface GetUser:
        UserViews.Username,
        UserViews.SubscriptionType,
        UserViews.StartedSeries,
        UserViews.PendingSeries,
        UserViews.FinishedSeries,
        UserViews.Id,
        GetSeries

    interface PostUser:
        UserViews.Username,
        UserViews.Password,
        UserViews.BankAccountIBAN,
        UserViews.SubscriptionType

    interface GetBills:
        BillViews.Date,
        BillViews.SubscriptionType,
        BillViews.Views,
        BillViews.Total,
        BillViews.Id,

        ViewViews.SeasonNumber,
        ViewViews.EpisodeNumber,
        ViewViews.TimeStamp,
        ViewViews.Cost,
        ViewViews.Series,
        ViewViews.Id,

        SeriesViews.Title,
        SeriesViews.SeriesType,
        SeriesViews.Id
}

class SeriesViews {
    interface Title
    interface Description
    interface Creators
    interface Actors
    interface SeriesType
    interface Episode
    interface Seasons
    interface EpisodePrice
    interface Id
}

class PersonViews {
    interface Name
    interface Surname
    interface SecondSurname
    interface Id
}

class SeasonViews {
    interface Number
    interface Episode
    interface Id
}

class EpisodeViews {
    interface Number
    interface Title
    interface Description
    interface Id
}

class UserViews {
    interface Username
    interface Password
    interface BankAccountIBAN
    interface SubscriptionType
    interface PersonalSpace
    interface Bills
    interface StartedSeries
    interface PendingSeries
    interface FinishedSeries
    interface Id
}

class ViewViews {
    interface SeasonNumber
    interface EpisodeNumber
    interface TimeStamp
    interface Cost
    interface Series
    interface Id
}

class BillViews {
    interface Date
    interface SubscriptionType
    interface Views
    interface Id
    interface Total
}
