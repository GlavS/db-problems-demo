--date: 2022-11-12
--author: stvort

delete from reviews;
delete from films_actors;
delete from films;
delete from directors;
delete from actors;
delete from genres;
dbcc checkident ('dbo.reviews', RESEED, 1);
dbcc checkident ('dbo.films', RESEED, 1);
dbcc checkident ('dbo.directors', RESEED, 1);
dbcc checkident ('dbo.actors', RESEED, 1);
dbcc checkident ('dbo.genres', RESEED, 1);

