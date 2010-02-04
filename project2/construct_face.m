function face = construct_face(avgface, eigfaces, coeffs)
len = length(coeffs)
for i = 1:len
  avgface = avgface + eigfaces(:, :, i) * coeffs(i)
end
face = avgface
end