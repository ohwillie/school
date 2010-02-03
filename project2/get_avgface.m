% Computes the average face for a cell array of STANDARDIZED faces.
function [avgface] = get_avgface(faces)
avgface = faces{1}; len = length(faces);
for i = 2:len
  avgface = avgface + faces{i};
end
avgface = (1 / len) * avgface;
end